package org.example.studyhub.service.impl;


import org.example.studyhub.model.Course;
import org.example.studyhub.repository.CourseRepository;
import org.example.studyhub.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

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
        String keywordPattern = (keyword == null || keyword.isBlank())
                ? null
                : "%" + keyword.trim().toLowerCase() + "%";

        return courseRepository.findPublicCoursesPaged(
                keywordPattern,
                categoryId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
    }

    @Override
    public List<Course> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructor_Id(instructorId);
    }

}
