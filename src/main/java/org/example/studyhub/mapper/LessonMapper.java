package org.example.studyhub.mapper;

import org.example.studyhub.dto.LessonDTO;
import org.example.studyhub.model.Chapter;
import org.example.studyhub.model.Lesson;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LessonMapper {

    public LessonDTO toDTO(Lesson lesson) {
        if (lesson == null) return null;

        LessonDTO dto = new LessonDTO();
        dto.setId(lesson.getId());
        dto.setTitle(lesson.getTitle());
        dto.setContentType(lesson.getContentType());
        dto.setContentUrl(lesson.getContentUrl());
        dto.setContentText(lesson.getContentText());
        dto.setDurationMinutes(lesson.getDurationMinutes());
        dto.setOrderNum(lesson.getOrderNum());
        dto.setIsPreview(lesson.getIsPreview());
        dto.setStatus(lesson.getStatus());
        dto.setCreatedAt(lesson.getCreatedAt());
        dto.setUpdatedAt(lesson.getUpdatedAt());

        if (lesson.getChapter() != null) {
            dto.setChapterId(lesson.getChapter().getId());
            dto.setChapterTitle(lesson.getChapter().getTitle());
        }

        return dto;
    }

    public Lesson toEntity(LessonDTO dto) {
        if (dto == null) return null;

        Lesson lesson = new Lesson();
        lesson.setTitle(dto.getTitle());
        lesson.setContentType(dto.getContentType());
        lesson.setContentUrl(dto.getContentUrl());
        lesson.setContentText(dto.getContentText());
        lesson.setDurationMinutes(dto.getDurationMinutes());
        lesson.setOrderNum(dto.getOrderNum());
        lesson.setIsPreview(dto.getIsPreview() != null ? dto.getIsPreview() : false);
        lesson.setStatus(dto.getStatus());

        if (dto.getChapterId() != null) {
            Chapter chapter = new Chapter();
            chapter.setId(dto.getChapterId());
            lesson.setChapter(chapter);
        }

        return lesson;
    }

    public List<LessonDTO> toDTOList(List<Lesson> lessons) {
        if (lessons == null) return null;
        return lessons.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Lesson updateEntity(Lesson existing, LessonDTO dto) {
        if (existing == null || dto == null) return existing;

        if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
        if (dto.getContentType() != null) existing.setContentType(dto.getContentType());
        if (dto.getContentUrl() != null) existing.setContentUrl(dto.getContentUrl());
        if (dto.getContentText() != null) existing.setContentText(dto.getContentText());
        if (dto.getDurationMinutes() != null) existing.setDurationMinutes(dto.getDurationMinutes());
        if (dto.getOrderNum() != null) existing.setOrderNum(dto.getOrderNum());
        if (dto.getIsPreview() != null) existing.setIsPreview(dto.getIsPreview());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        if (dto.getChapterId() != null) {
            Chapter chapter = new Chapter();
            chapter.setId(dto.getChapterId());
            existing.setChapter(chapter);
        }

        return existing;
    }
}