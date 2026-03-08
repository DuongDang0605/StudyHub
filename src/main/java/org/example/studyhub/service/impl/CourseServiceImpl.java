package org.example.studyhub.service.impl;

import org.example.studyhub.model.Course;
import org.example.studyhub.repository.CourseRepository;
import org.example.studyhub.service.CourseService;
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
}
