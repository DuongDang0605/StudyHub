package org.example.studyhub.service;

import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.model.Course;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<Course> getAllCourses();
    List<Course> findPublicCourses(String keyword, Long categoryId);
    Page<Course> findPublicCoursesPaged(String keyword, Long categoryId, int page, int size);
    Course getCourseDetailForPublic(Long id);
    List<Course> getActiveCoursesByStatus();
    List<Course> getCoursesByInstructor(Long instructorId);
    //CRUD danh sách
    List<CourseDTO> getAllCourses1();
    Optional<CourseDTO> getCourseById(Long id);
    CourseDTO createCourse(CourseDTO courseDTO);
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    void deleteCourse(Long id);

    // Entity conversion
    CourseDTO convertToDTO(Course course);
    Course convertToEntity(CourseDTO courseDTO);

}
