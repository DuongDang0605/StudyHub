package org.example.studyhub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterDTO {

    private Long id;
    private Long courseId;
    private String courseTitle;
    private String title;
    private String description;
    private Integer orderNum;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}