package org.example.studyhub.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "setting",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "type_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    // Self-reference: type_id = NULL means this IS a type; type_id != NULL means this is a value of that type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Setting type;

    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    private Set<Setting> children;

    @Column(name = "value", length = 100)
    private String value;

    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    @Column(name = "description", length = 200)
    private String description;

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