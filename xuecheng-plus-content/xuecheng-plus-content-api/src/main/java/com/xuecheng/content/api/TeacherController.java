package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class TeacherController {

    @Autowired
    CourseTeacherService courseTeacherService;

    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getTeachers(@PathVariable Long courseId) {
        return courseTeacherService.getTeachers(courseId);
    }

    @PostMapping("/courseTeacher")
    public CourseTeacher addTeacher(@RequestBody CourseTeacher teacher) {
        return courseTeacherService.addTeacher(teacher);
    }

    @PutMapping("/courseTeacher")
    public CourseTeacher modifyTeacher(@RequestBody CourseTeacher teacher) {
        return courseTeacherService.modifyTeacher(teacher);
    }

    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteTeacher(@PathVariable Long courseId, @PathVariable Long id) {
         courseTeacherService.deleteTeacher(courseId, id);
    }
}
