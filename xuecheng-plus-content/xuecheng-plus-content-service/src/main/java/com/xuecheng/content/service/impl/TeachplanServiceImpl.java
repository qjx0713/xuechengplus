package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> getTreeNodes(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {

        //课程计划id
        Long id = teachplanDto.getId();
        //修改课程计划
        if (id != null) {
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        } else {
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count + 1);
            BeanUtils.copyProperties(teachplanDto, teachplanNew);

            teachplanMapper.insert(teachplanNew);

        }

    }

    @Override
    public void deleteTeachPlan(Long teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        //一级章节
        if (teachplan.getGrade() == 1) {
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, teachplan.getId());
            Integer count = teachplanMapper.selectCount(queryWrapper);
            if (count > 0) {//有子章节不能删除
                XueChengPlusException.cast("120409", "课程计划信息还有子级信息，无法操作");
            } else {
                teachplanMapper.deleteById(teachPlanId);
            }
        } else {//二级章节
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplan.getId());
            TeachplanMedia teachplanMedia = teachplanMediaMapper.selectOne(queryWrapper);
            if (teachplanMedia != null) {
                teachplanMediaMapper.deleteById(teachplanMedia.getId());
            }
        }

    }

    @Transactional
    @Override
    public void teachPlanOrder(String action, Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        //选出和当前排序同级的
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getGrade, teachplan.getGrade());
        queryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());
        queryWrapper.eq(teachplan.getGrade() == 2, Teachplan::getParentid, teachplan.getParentid());
        queryWrapper.orderByAsc(Teachplan::getOrderby);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        int index = 0;
        for (int i = 0; i < teachplans.size(); i++) {
            if (teachplans.get(i).getId().equals(teachplan.getId())) {
                index = i;
            }
        }

        if (action.equals("moveup") && index == 0) {
            XueChengPlusException.cast("已经是第一个了");
        }
        if (action.equals("movedown") && index == teachplans.size() - 1) {
            XueChengPlusException.cast("已经是最后一个了");
        }

        if (action.equals("moveup")) {
            teachplan.setOrderby(teachplan.getOrderby() - 1);
            Teachplan prePlan = teachplans.get(index - 1);
            prePlan.setOrderby(prePlan.getOrderby() + 1);
            teachplanMapper.updateById(teachplan);
            teachplanMapper.updateById(prePlan);
            return;
        }
        if (action.equals("movedown")) {
            teachplan.setOrderby(teachplan.getOrderby() + 1);
            Teachplan nextPlan = teachplans.get(index + 1);
            nextPlan.setOrderby(nextPlan.getOrderby() - 1);
            teachplanMapper.updateById(teachplan);
            teachplanMapper.updateById(nextPlan);
        }


    }

    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //教学计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            XueChengPlusException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //课程id
        Long courseId = teachplan.getCourseId();

        //先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));

        //再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    @Override
    public void deteleMedia(Long teachPlanId, String mediaId) {
        LambdaQueryWrapper<TeachplanMedia> teachplanMediaLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId);
        teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getMediaId, mediaId);
        int delete = teachplanMediaMapper.delete(teachplanMediaLambdaQueryWrapper);
        if (delete < 0) {
            XueChengPlusException.cast("删除视频失败");
        }
    }

    /**
     * @param courseId 课程id
     * @param parentId 父课程计划id
     * @return int 最新排序号
     * @description 获取最新的排序号
     */
    private int getTeachplanCount(long courseId, long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
