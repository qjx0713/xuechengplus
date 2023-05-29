package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;
    //课程计划查询
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        List<TeachplanDto> list = teachplanService.getTreeNodes(courseId);
        return list;
    }

    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachPlan(@PathVariable Long id) {
        teachplanService.deleteTeachPlan(id);
    }

    @PostMapping("/teachplan/{action}/{id}")
    public void teachPlanOrder(@PathVariable String action, @PathVariable Long id) {
        teachplanService.teachPlanOrder(action, id);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    @ApiOperation(value = "删除课程计划和媒资信息绑定")
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public void deteleMedia(@PathVariable Long teachPlanId, @PathVariable String mediaId){
        teachplanService.deteleMedia(teachPlanId, mediaId);
    }
}
