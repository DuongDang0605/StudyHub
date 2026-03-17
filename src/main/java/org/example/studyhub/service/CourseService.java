package org.example.studyhub.service;

import org.example.studyhub.model.Course;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    List<Course> findPublicCourses(String keyword, Long categoryId);
    Page<Course> findPublicCoursesPaged(String keyword, Long categoryId, int page, int size);
    List<Course> getActiveCoursesByStatus();
    List<Course> getCoursesByInstructor(Long instructorId);
}
