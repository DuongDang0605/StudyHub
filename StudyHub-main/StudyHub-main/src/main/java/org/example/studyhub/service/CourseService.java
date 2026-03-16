package org.example.studyhub.service;

import java.util.List;
import java.util.Optional;

import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.model.Course;

public interface CourseService {
    
    //CRUD danh sách
    List<CourseDTO> getAllCourses();
    Optional<CourseDTO> getCourseById(Long id);
    CourseDTO createCourse(CourseDTO courseDTO);
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    void deleteCourse(Long id);
    
    // Entity conversion
    CourseDTO convertToDTO(Course course);
    Course convertToEntity(CourseDTO courseDTO);
}
