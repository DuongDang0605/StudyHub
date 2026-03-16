package org.example.studyhub.service;

import org.example.studyhub.model.Course;

import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    List<Course> findPublicCourses(String keyword, Long categoryId);
    List<Course> getActiveCoursesByStatus();
}
