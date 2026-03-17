package org.example.studyhub.repository;

import org.example.studyhub.model.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Query("""
            SELECT e
            FROM Enrollment e
            WHERE e.user.id = :userId
              OR ( :userEmail IS NOT NULL
                   AND e.email IS NOT NULL
                   AND LOWER(e.email) = LOWER(:userEmail) )
            """)
    Page<Enrollment> findByUserIdOrEmail(@Param("userId") Long userId,
                                         @Param("userEmail") String userEmail,
                                         Pageable pageable);

    @Query("""
            SELECT e
            FROM Enrollment e
            WHERE (e.user.id = :userId
                   OR ( :userEmail IS NOT NULL
                        AND e.email IS NOT NULL
                        AND LOWER(TRIM(e.email)) = LOWER(TRIM(:userEmail)) ))
              AND TRIM(UPPER(COALESCE(e.status, ''))) = 'APPROVED'
              AND (:keywordPattern IS NULL OR LOWER(e.course.title) LIKE :keywordPattern)
            """)
    Page<Enrollment> findApprovedByUserId(@Param("userId") Long userId,
                                          @Param("userEmail") String userEmail,
                                          @Param("keywordPattern") String keywordPattern,
                                          Pageable pageable);

    @Query("""
            SELECT e
            FROM Enrollment e
            WHERE TRIM(UPPER(COALESCE(e.status, ''))) = 'APPROVED'
              AND (:keywordPattern IS NULL OR LOWER(e.course.title) LIKE :keywordPattern)
            """)
    Page<Enrollment> findAllApproved(@Param("keywordPattern") String keywordPattern,
                                     Pageable pageable);

    @Query("""
            SELECT e
            FROM Enrollment e
            WHERE TRIM(UPPER(COALESCE(e.status, ''))) = 'APPROVED'
              AND e.course.instructor.id = :instructorId
              AND (:keywordPattern IS NULL OR LOWER(e.course.title) LIKE :keywordPattern)
            """)
    Page<Enrollment> findApprovedByInstructor(@Param("instructorId") Long instructorId,
                                              @Param("keywordPattern") String keywordPattern,
                                              Pageable pageable);

    Optional<Enrollment> findByOrderCode(Long orderCode);

    @Query("SELECT e FROM Enrollment e WHERE " +
            "(:courseId IS NULL OR e.course.id = :courseId) AND " +
            "(:status IS NULL OR :status = '' OR :status = 'All' OR e.status = :status) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            " LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Enrollment> findAllWithFilter(@Param("courseId") Long courseId,
                                       @Param("status") String status,
                                       @Param("keyword") String keyword);
}
