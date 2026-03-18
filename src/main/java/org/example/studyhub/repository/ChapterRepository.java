package org.example.studyhub.repository;

import org.example.studyhub.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    // Hàm này giúp lấy Chapter kèm theo TẤT CẢ Lesson của nó ngay lập tức
    @Query("SELECT DISTINCT c FROM Chapter c LEFT JOIN FETCH c.lessons WHERE c.course.id = :courseId ORDER BY c.orderNum ASC")
    List<Chapter> findByCourseIdWithLessons(@Param("courseId") Long courseId);
}
