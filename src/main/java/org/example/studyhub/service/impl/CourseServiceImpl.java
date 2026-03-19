package org.example.studyhub.service.impl;


import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.model.Course;
import org.example.studyhub.model.Setting;
import org.example.studyhub.model.User;
import org.example.studyhub.repository.CourseRepository;
import org.example.studyhub.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAllByOrderByTitleAsc();
    }

    @Override
    public List<Course> getActiveCoursesByStatus() {
        return courseRepository.findAllByStatus("PUBLISHED");
    }

    @Override
    public List<Course> findPublicCourses(String keyword, Long categoryId) {
        String k = (keyword == null) ? "" : keyword.trim();
        return courseRepository.findPublicCourses(k, categoryId);
    }

    @Override
    public Page<Course> findPublicCoursesPaged(String keyword, Long categoryId, int page, int size) {
        String cleanKeyword = (keyword == null || keyword.isBlank())
                ? ""
                : keyword.trim().toLowerCase();

        Long validCategoryId = (categoryId != null && categoryId > 0) ? categoryId : null;
        int actualPage = Math.max(0, page);

        return courseRepository.findPublicCoursesPaged(
                cleanKeyword,
                validCategoryId,
                PageRequest.of(actualPage, size)
        );
    }

    @Override
    public List<Course> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructor_Id(instructorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses1() {
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
    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        System.out.println("=== SERVICE CREATE COURSE ===");
        System.out.println("Input CourseDTO: " + courseDTO);

        Course course = convertToEntity(courseDTO);
        System.out.println("Converted Course entity: " + course);
        System.out.println("Entity createdAt before save: " + course.getCreatedAt());

        Course savedCourse = courseRepository.save(course);
        System.out.println("Saved Course entity: " + savedCourse);
        System.out.println("Saved Course ID: " + savedCourse.getId());
        System.out.println("Saved Course createdAt: " + savedCourse.getCreatedAt());

        CourseDTO result = convertToDTO(savedCourse);
        System.out.println("Result CourseDTO createdAt: " + result.getCreatedAt());

        return result;
    }
    @Override
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        System.out.println("=== UPDATE SERVICE DEBUG ===");
        System.out.println("Input CourseDTO: " + courseDTO);
        System.out.println("isFeatured: " + courseDTO.getIsFeatured());
        System.out.println("status: " + courseDTO.getStatus());

        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        // Update fields with null handling
        existingCourse.setTitle(courseDTO.getTitle());
        existingCourse.setDescription(courseDTO.getDescription());
        existingCourse.setThumbnail(courseDTO.getThumbnail());
        existingCourse.setListedPrice(courseDTO.getListedPrice());
        existingCourse.setSalePrice(courseDTO.getSalePrice());
        existingCourse.setDuration(courseDTO.getDuration());
        existingCourse.setStatus(courseDTO.getStatus() != null ? courseDTO.getStatus() : existingCourse.getStatus());
        existingCourse.setIsFeatured(courseDTO.getIsFeatured() != null ? courseDTO.getIsFeatured() : existingCourse.getIsFeatured());

        // Update level
        if (courseDTO.getLevelId() != null) {
            Setting level = new Setting();
            level.setId(courseDTO.getLevelId());
            existingCourse.setLevel(level);
        }

        // Update category
        if (courseDTO.getCategoryId() != null) {
            Setting category = new Setting();
            category.setId(courseDTO.getCategoryId());
            existingCourse.setCategory(category);
        }

        // Update instructor
        if (courseDTO.getInstructorId() != null) {
            User instructor = new User();
            instructor.setId(courseDTO.getInstructorId());
            existingCourse.setInstructor(instructor);
        }

        System.out.println("Updated Course entity: " + existingCourse);

        Course updatedCourse = courseRepository.save(existingCourse);
        System.out.println("Course saved successfully");

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
        Course course = Course.builder()
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .thumbnail(courseDTO.getThumbnail())
                .listedPrice(courseDTO.getListedPrice() != null ? courseDTO.getListedPrice() : BigDecimal.ZERO)
                .salePrice(courseDTO.getSalePrice() != null ? courseDTO.getSalePrice() : BigDecimal.ZERO)
                .duration(courseDTO.getDuration())
                .status(courseDTO.getStatus() != null ? courseDTO.getStatus() : "UNPUBLISHED")
                .isFeatured(courseDTO.getIsFeatured() != null ? courseDTO.getIsFeatured() : false)
                .build();

        // Handle level
        if (courseDTO.getLevelId() != null) {
            Setting level = new Setting();
            level.setId(courseDTO.getLevelId());
            course.setLevel(level);
        }

        // Handle category
        if (courseDTO.getCategoryId() != null) {
            Setting category = new Setting();
            category.setId(courseDTO.getCategoryId());
            course.setCategory(category);
        }

        // Handle instructor
        if (courseDTO.getInstructorId() != null) {
            User instructor = new User();
            instructor.setId(courseDTO.getInstructorId());
            course.setInstructor(instructor);
        }

        return course;
    }
    @Override
    public Course getCourseDetailForPublic(Long id) {
        return courseRepository.findByIdAndStatus(id, "PUBLISHED").orElse(null);
    }
}
