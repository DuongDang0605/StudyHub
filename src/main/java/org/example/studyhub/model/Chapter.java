package org.example.studyhub.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "chapter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "order_num", nullable = false)
    private Integer orderNum = 1;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Lesson> lessons;

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