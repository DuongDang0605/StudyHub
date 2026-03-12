package org.example.studyhub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {

    private Long id;
    private Long courseId;
    private String courseTitle;
    private Long userId;
    @NotBlank(message = "Họ và tên không được để trống")
    private String  fullName;
    private String email;
    private String mobile;
    private BigDecimal fee;
    private String paymentMethod;
    private String enrollNote;
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
    private String rejectedNotes;
    private BigDecimal progress;
    private LocalDateTime completedAt;
    private LocalDateTime enrolledAt;
    private LocalDateTime updatedAt;
}