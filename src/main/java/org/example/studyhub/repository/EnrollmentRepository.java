package org.example.studyhub.repository;

import org.example.studyhub.model.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query(value = "SELECT e.* FROM enrollment e " +
            "JOIN course c ON e.course_id = c.course_id " +
            "WHERE (:courseId IS NULL OR e.course_id = :courseId) " +
            "AND (:status IS NULL OR e.status = :status) " +
            "AND (:keyword IS NULL OR " +
            "   e.full_name ILIKE %:keyword% OR " +
            "   e.email ILIKE %:keyword% OR " +
            "   c.title ILIKE %:keyword%) " +
            "AND (:managerId IS NULL OR c.instructor_id = :managerId)", // Lọc theo Manager nếu có
            countQuery = "SELECT count(*) FROM enrollment e " +
                    "JOIN course c ON e.course_id = c.course_id " +
                    "WHERE (:courseId IS NULL OR e.course_id = :courseId) " +
                    "AND (:status IS NULL OR e.status = :status) " +
                    "AND (:keyword IS NULL OR e.full_name ILIKE %:keyword%) " +
                    "AND (:managerId IS NULL OR c.instructor_id = :managerId)",
            nativeQuery = true)
    Page<Enrollment> searchEnrollments(
            @Param("courseId") Long courseId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("managerId") Long managerId,
            Pageable pageable);
}