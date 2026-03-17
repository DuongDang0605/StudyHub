package org.example.studyhub.repository;

import org.example.studyhub.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter,Long> {
    List<Chapter> findByCourseIdOrderByOrderNum(Long courseId);
}
