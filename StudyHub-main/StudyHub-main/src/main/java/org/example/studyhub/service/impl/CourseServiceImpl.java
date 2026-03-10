package org.example.studyhub.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.model.Course;
import org.example.studyhub.repository.CourseRepository;
import org.example.studyhub.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseDTO> getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = convertToEntity(courseDTO);
        Course savedCourse = courseRepository.save(course);
        return convertToDTO(savedCourse);
    }

    @Override
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        
        // Update fields
        existingCourse.setTitle(courseDTO.getTitle());
        existingCourse.setDescription(courseDTO.getDescription());
        existingCourse.setThumbnail(courseDTO.getThumbnail());
        existingCourse.setListedPrice(courseDTO.getListedPrice());
        existingCourse.setSalePrice(courseDTO.getSalePrice());
        existingCourse.setDuration(courseDTO.getDuration());
        existingCourse.setStatus(courseDTO.getStatus());
        existingCourse.setIsFeatured(courseDTO.getIsFeatured());
        
        Course updatedCourse = courseRepository.save(existingCourse);
        return convertToDTO(updatedCourse);
    }

    @Override
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    @Override
    public CourseDTO convertToDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .thumbnail(course.getThumbnail())
                .levelId(course.getLevel() != null ? course.getLevel().getId() : null)
                .levelName(course.getLevel() != null ? course.getLevel().getValue() : null)
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getValue() : null)
                .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                .instructorName(course.getInstructor() != null ? course.getInstructor().getFullName() : null)
                .listedPrice(course.getListedPrice())
                .salePrice(course.getSalePrice())
                .duration(course.getDuration())
                .status(course.getStatus())
                .isFeatured(course.getIsFeatured())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    @Override
    public Course convertToEntity(CourseDTO courseDTO) {
        return Course.builder()
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .thumbnail(courseDTO.getThumbnail())
                .listedPrice(courseDTO.getListedPrice())
                .salePrice(courseDTO.getSalePrice())
                .duration(courseDTO.getDuration())
                .status(courseDTO.getStatus())
                .isFeatured(courseDTO.getIsFeatured())
                .build();
    }
}
