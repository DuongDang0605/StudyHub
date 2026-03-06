package org.example.studyhub.repository;

import org.example.studyhub.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter,Long> {
}
