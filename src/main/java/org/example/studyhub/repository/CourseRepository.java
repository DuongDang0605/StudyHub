package org.example.studyhub.repository;

import org.example.studyhub.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course,Long> {

    long countByStatus(String status);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.instructor LEFT JOIN FETCH c.category ORDER BY c.createdAt DESC")
    Page<Course> findTopRecentCourses(Pageable pageable);
    List<Course> findAllByOrderByTitleAsc();

    @Query("""
    SELECT c FROM Course c
    LEFT JOIN FETCH c.instructor
    LEFT JOIN FETCH c.level
    LEFT JOIN FETCH c.category
    WHERE c.status = 'PUBLISHED'
      AND (:keyword = '' OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
      OR LOWER(COALESCE(c.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:categoryId IS NULL OR c.category.id = :categoryId)
    ORDER BY c.createdAt DESC
""")
    List<Course> findPublicCourses(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);

    @Query(value = """
   SELECT c
   FROM Course c
   LEFT JOIN c.category cat
   WHERE c.status = 'PUBLISHED'
     AND (:keywordPattern IS NULL
          OR LOWER(c.title) LIKE :keywordPattern
          OR LOWER(COALESCE(c.description, '')) LIKE :keywordPattern)
     AND (:categoryId IS NULL OR cat.id = :categoryId)
   ORDER BY c.createdAt DESC
   """,
            countQuery = """
   SELECT COUNT(c)
   FROM Course c
   LEFT JOIN c.category cat
   WHERE c.status = 'PUBLISHED'
     AND (:keywordPattern IS NULL
          OR LOWER(c.title) LIKE :keywordPattern
          OR LOWER(COALESCE(c.description, '')) LIKE :keywordPattern)
     AND (:categoryId IS NULL OR cat.id = :categoryId)
   """)
    Page<Course> findPublicCoursesPaged(@Param("keywordPattern") String keywordPattern,
                                        @Param("categoryId") Long categoryId,
                                        Pageable pageable);
    List<Course> findAllByStatus(String status);
    Optional<Course> findByEnrollmentsId(Long enrollmentsId);

    List<Course> findByInstructor_Id(Long instructorId);

}
