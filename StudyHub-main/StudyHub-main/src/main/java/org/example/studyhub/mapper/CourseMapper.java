package org.example.studyhub.mapper;

import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.model.Course;
import org.example.studyhub.model.Setting;
import org.example.studyhub.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    public CourseDTO toDTO(Course course) {
        if (course == null) return null;

        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setThumbnail(course.getThumbnail());
        dto.setListedPrice(course.getListedPrice());
        dto.setSalePrice(course.getSalePrice());
        dto.setDuration(course.getDuration());
        dto.setStatus(course.getStatus());
        dto.setIsFeatured(course.getIsFeatured());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());

        if (course.getLevel() != null) {
            dto.setLevelId(course.getLevel().getId());
            dto.setLevelName(course.getLevel().getName());
        }

        if (course.getCategory() != null) {
            dto.setCategoryId(course.getCategory().getId());
            dto.setCategoryName(course.getCategory().getName());
        }

        if (course.getInstructor() != null) {
            dto.setInstructorId(course.getInstructor().getId());
            dto.setInstructorName(course.getInstructor().getFullName());
        }

        return dto;
    }

    public Course toEntity(CourseDTO dto) {
        if (dto == null) return null;

        Course course = new Course();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setThumbnail(dto.getThumbnail());
        course.setListedPrice(dto.getListedPrice());
        course.setSalePrice(dto.getSalePrice());
        course.setDuration(dto.getDuration());
        course.setStatus(dto.getStatus());
        course.setIsFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);

        if (dto.getLevelId() != null) {
            Setting level = new Setting();
            level.setId(dto.getLevelId());
            course.setLevel(level);
        }

        if (dto.getCategoryId() != null) {
            Setting category = new Setting();
            category.setId(dto.getCategoryId());
            course.setCategory(category);
        }

        if (dto.getInstructorId() != null) {
            User instructor = new User();
            instructor.setId(dto.getInstructorId());
            course.setInstructor(instructor);
        }

        return course;
    }

    public List<CourseDTO> toDTOList(List<Course> courses) {
        if (courses == null) return null;
        return courses.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Course updateEntity(Course existing, CourseDTO dto) {
        if (existing == null || dto == null) return existing;

        if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getThumbnail() != null) existing.setThumbnail(dto.getThumbnail());
        if (dto.getListedPrice() != null) existing.setListedPrice(dto.getListedPrice());
        if (dto.getSalePrice() != null) existing.setSalePrice(dto.getSalePrice());
        if (dto.getDuration() != null) existing.setDuration(dto.getDuration());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getIsFeatured() != null) existing.setIsFeatured(dto.getIsFeatured());

        if (dto.getLevelId() != null) {
            Setting level = new Setting();
            level.setId(dto.getLevelId());
            existing.setLevel(level);
        }

        if (dto.getCategoryId() != null) {
            Setting category = new Setting();
            category.setId(dto.getCategoryId());
            existing.setCategory(category);
        }

        if (dto.getInstructorId() != null) {
            User instructor = new User();
            instructor.setId(dto.getInstructorId());
            existing.setInstructor(instructor);
        }

        return existing;
    }
}