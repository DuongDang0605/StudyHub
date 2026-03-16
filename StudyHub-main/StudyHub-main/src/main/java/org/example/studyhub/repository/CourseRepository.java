package org.example.studyhub.repository;

import java.util.List;

import org.example.studyhub.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Basic operations

    // Count by status (for statistics if needed)
    long countByStatus(String status);
}
