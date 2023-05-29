package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> getTeachers(Long courseId) {

        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        return courseTeachers;
    }

    @Override
    public CourseTeacher addTeacher(CourseTeacher teacher) {
        teacher.setCreateDate(LocalDateTime.now());
        int i = courseTeacherMapper.insert(teacher);
        if (i <= 0) {
            XueChengPlusException.cast("添加教师信息失败");
        }

        return courseTeacherMapper.selectById(teacher.getId());
    }

    @Override
    public CourseTeacher modifyTeacher(CourseTeacher teacher) {
        teacher.setCreateDate(LocalDateTime.now());
        int i = courseTeacherMapper.updateById(teacher);
        if (i <= 0) {
            XueChengPlusException.cast("修改教师信息失败");
        }

        return courseTeacherMapper.selectById(teacher.getId());
    }

    @Override
    public void deleteTeacher(Long courseId, Long id) {
        int i = courseTeacherMapper.deleteById(id);
        if (i <= 0) {
            XueChengPlusException.cast("删除教师信息失败");
        }
    }
}
