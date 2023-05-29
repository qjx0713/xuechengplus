package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    public List<CourseTeacher> getTeachers(Long courseId);

    CourseTeacher addTeacher(CourseTeacher teacher);

    CourseTeacher modifyTeacher(CourseTeacher teacher);

    void deleteTeacher(Long courseId, Long id);
}
