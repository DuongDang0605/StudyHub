package org.example.studyhub.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "UNVERIFIED"; // UNVERIFIED, ACTIVE, INACTIVE

    @Column(name = "email_verify_token", length = 255)
    private String emailVerifyToken;

    @Column(name = "email_verify_expired_at")
    private LocalDateTime emailVerifyExpiredAt;

    @Column(name = "reset_password_token", length = 255)
    private String resetPasswordToken;

    @Column(name = "reset_password_expired_at")
    private LocalDateTime resetPasswordExpiredAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments;

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