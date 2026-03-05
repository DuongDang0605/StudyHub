package org.example.studyhub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDTO {

    private Long id;
    private Long chapterId;
    private String chapterTitle;
    private String title;
    private String contentType; // VIDEO, PDF, TEXT
    private String contentUrl;
    private String contentText;
    private Integer durationMinutes;
    private Integer orderNum;
    private Boolean isPreview;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}