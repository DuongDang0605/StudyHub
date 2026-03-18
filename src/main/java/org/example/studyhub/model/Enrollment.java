package org.example.studyhub.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Nullable: Guest user enrolls without an account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Actual learner info (can be different from the logged-in user)
    @Column(name = "full_name", columnDefinition = "TEXT")
    private String fullName;

    @Column(name = "email", columnDefinition = "TEXT")
    private String email;
    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal fee = BigDecimal.ZERO;

    // BANK_TRANSFER (PayOS), INTERNET_BANKING (VnPay)
    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @Column(name = "enroll_note", columnDefinition = "TEXT")
    private String enrollNote;

    // PENDING, APPROVED, REJECTED, CANCELLED
    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "rejected_notes", columnDefinition = "TEXT")
    private String rejectedNotes;

    @Column(name = "progress", nullable = false, precision = 5, scale = 2)
    private BigDecimal progress = BigDecimal.ZERO;

    @Column(name = "completed_at")
    private LocalDate completedAt;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @Column(name = "order_code", unique = true)
    private Long orderCode;

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}