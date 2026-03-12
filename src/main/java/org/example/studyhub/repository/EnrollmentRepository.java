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

    @Query("SELECT e FROM Enrollment e " +
            "WHERE (:courseId IS NULL OR e.course.id = :courseId) " +
            "AND (:status IS NULL OR e.status = :status) " +
            "AND (:keyword IS NULL OR " +
            "   e.fullName LIKE %:keyword% OR " +
            "   e.email LIKE %:keyword% OR " +
            "   e.course.title LIKE %:keyword%) " +
            "AND (:managerId IS NULL OR e.course.instructor.id = :managerId)")
    Page<Enrollment> searchEnrollments(
            @Param("courseId") Long courseId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("managerId") Long managerId,
            Pageable pageable);

    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId " +
            "AND (:keyword IS NULL OR e.course.title LIKE %:keyword%)")
    Page<Enrollment> findByUserId(@Param("userId") Long userId,
                                  @Param("keyword") String keyword,
                                  Pageable pageable);
}