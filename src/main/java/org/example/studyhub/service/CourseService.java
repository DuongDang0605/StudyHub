package org.example.studyhub.service;

import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.model.Course;

import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    List<Course> getActiveCoursesByStatus();
    List<Course> getCoursesByInstructor(Long instructorId);
}
