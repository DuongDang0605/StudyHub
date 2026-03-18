package org.example.studyhub.service;

import org.example.studyhub.model.Lesson;
import java.util.List;

public interface LessonService {
    List<Lesson> getLessonsByChapter(Long chapterId);
    Lesson getLessonById(Long id);
    Lesson createLesson(Lesson lesson);
    Lesson updateLesson(Long id, Lesson lesson);
    void deleteLesson(Long id);
}