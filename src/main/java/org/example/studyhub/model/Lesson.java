package org.example.studyhub.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    // VIDEO, PDF, TEXT
    @Column(name = "content_type", nullable = false, length = 20)
    private String contentType;

    // YouTube URL or direct file URL (for VIDEO/PDF)
    @Column(name = "content_url", length = 500)
    private String contentUrl;

    // Rich-text content (for TEXT type)
    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;

    @Column(name = "duration_minutes")
    private Integer durationMinutes = 0;

    @Column(name = "order_num", nullable = false)
    private Integer orderNum = 1;

    // Preview lessons can be watched without enrollment
    @Column(name = "is_preview", nullable = false)
    private Boolean isPreview = false;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}