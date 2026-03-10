package org.example.studyhub.mapper;

import org.example.studyhub.dto.ChapterDTO;
import org.example.studyhub.model.Chapter;
import org.example.studyhub.model.Course;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChapterMapper {

    public ChapterDTO toDTO(Chapter chapter) {
        if (chapter == null) return null;

        ChapterDTO dto = new ChapterDTO();
        dto.setId(chapter.getId());
        dto.setTitle(chapter.getTitle());
        dto.setDescription(chapter.getDescription());
        dto.setOrderNum(chapter.getOrderNum());
        dto.setStatus(chapter.getStatus());
        dto.setCreatedAt(chapter.getCreatedAt());
        dto.setUpdatedAt(chapter.getUpdatedAt());

        if (chapter.getCourse() != null) {
            dto.setCourseId(chapter.getCourse().getId());
            dto.setCourseTitle(chapter.getCourse().getTitle());
        }

        return dto;
    }

    public Chapter toEntity(ChapterDTO dto) {
        if (dto == null) return null;

        Chapter chapter = new Chapter();
        chapter.setTitle(dto.getTitle());
        chapter.setDescription(dto.getDescription());
        chapter.setOrderNum(dto.getOrderNum());
        chapter.setStatus(dto.getStatus());

        if (dto.getCourseId() != null) {
            Course course = new Course();
            course.setId(dto.getCourseId());
            chapter.setCourse(course);
        }

        return chapter;
    }

    public List<ChapterDTO> toDTOList(List<Chapter> chapters) {
        if (chapters == null) return null;
        return chapters.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Chapter updateEntity(Chapter existing, ChapterDTO dto) {
        if (existing == null || dto == null) return existing;

        if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getOrderNum() != null) existing.setOrderNum(dto.getOrderNum());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        if (dto.getCourseId() != null) {
            Course course = new Course();
            course.setId(dto.getCourseId());
            existing.setCourse(course);
        }

        return existing;
    }
}