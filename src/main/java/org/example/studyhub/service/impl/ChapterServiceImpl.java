
package org.example.studyhub.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.studyhub.model.Chapter;
import org.example.studyhub.repository.ChapterRepository;
import org.example.studyhub.service.ChapterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;

    @Override
    public List<Chapter> getChaptersByCourse(Long courseId) {
        return chapterRepository.findByCourseIdOrderByOrderNum(courseId);
    }

    @Override
    public Chapter createChapter(Chapter chapter) {
        return chapterRepository.save(chapter);
    }

    @Override
    public Chapter updateChapter(Long id, Chapter chapter) {

        Chapter existing = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        existing.setTitle(chapter.getTitle());
        existing.setDescription(chapter.getDescription());
        existing.setOrderNum(chapter.getOrderNum());

        return chapterRepository.save(existing);
    }

    @Override
    public void deleteChapter(Long id) {
        chapterRepository.deleteById(id);
    }
}