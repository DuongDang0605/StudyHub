package org.example.studyhub.repository;

import org.example.studyhub.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course,Long> {

    long countByStatus(String status);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.instructor LEFT JOIN FETCH c.category ORDER BY c.createdAt DESC")
    Page<Course> findTopRecentCourses(Pageable pageable);
    List<Course> findAllByOrderByTitleAsc();

    List<Course> findAllByStatus(String published);
    Optional<Course> findByEnrollmentsId(Long enrollmentsId);

    List<Course> findByInstructor_Id(Long instructorId);
}
