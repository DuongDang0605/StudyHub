package org.example.studyhub.service.impl;

import org.example.studyhub.model.Lesson;
import org.example.studyhub.repository.LessonRepository;
import org.example.studyhub.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public List<Lesson> getLessonsByChapter(Long chapterId) {
        return lessonRepository.findByChapterIdOrderByOrderNumAsc(chapterId);
    }

    @Override
    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + id));
    }

    @Override
    public Lesson createLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    @Override
    public Lesson updateLesson(Long id, Lesson lesson) {
        Lesson existing = getLessonById(id);
        existing.setTitle(lesson.getTitle());
        existing.setContentType(lesson.getContentType());
        existing.setContentUrl(lesson.getContentUrl());
        existing.setContentText(lesson.getContentText());
        existing.setDurationMinutes(lesson.getDurationMinutes());
        existing.setOrderNum(lesson.getOrderNum());
        existing.setIsPreview(lesson.getIsPreview());
        return lessonRepository.save(existing);
    }

    @Override
    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }
}