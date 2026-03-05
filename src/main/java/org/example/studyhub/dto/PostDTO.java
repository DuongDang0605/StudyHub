package org.example.studyhub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {

    private Long id;
    private String title;
    private String content;
    private String thumbnail;
    private Long authorId;
    private String authorName;
    private Long categoryId;
    private String categoryName;
    private String status; // DRAFT, PUBLISHED, HIDDEN
    private Boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}