package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

public interface TeachplanService {
    public List<TeachplanDto> getTreeNodes(Long courseId);

    public void saveTeachplan(SaveTeachplanDto teachplanDto);
    public void deleteTeachPlan(Long teachPlanId);

    void teachPlanOrder(String action, Long id);
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    void deteleMedia(Long teachPlanId, String mediaId);
}
