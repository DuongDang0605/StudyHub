package org.example.studyhub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long id;
    private Long postId;
    private Long userId;
    private String userFullName;
    private String userAvatar;
    private Long parentId;
    private String content;
    private String status; // ACTIVE, DELETED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}