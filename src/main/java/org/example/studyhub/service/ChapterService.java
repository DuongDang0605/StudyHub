package org.example.studyhub.service;

import org.example.studyhub.model.Chapter;

import java.util.List;

public interface ChapterService {
    List<Chapter> getChaptersByCourse(Long courseId);

    Chapter createChapter(Chapter chapter);

    Chapter updateChapter(Long id, Chapter chapter);

    void deleteChapter(Long id);
}
