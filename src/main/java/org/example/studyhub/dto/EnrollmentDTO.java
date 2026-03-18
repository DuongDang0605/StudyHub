package org.example.studyhub.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {

    private Long id;
    @NotNull(message = "Vui lòng chọn một khóa học")
    private Long courseId;
    private String courseTitle;
    private Long userId;
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String fullName;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng (ví dụ: abc@gmail.com)")
    private String email;
    private String mobile;
    @NotNull(message = "Học phí không được để trống")
    @DecimalMin(value = "5000", message = "Học phí tối thiểu là 5,000 VND")
    private BigDecimal fee;
    private String paymentMethod;
    private String enrollNote;
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
    private String rejectedNotes;
    private BigDecimal progress;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate completedAt;
    private LocalDateTime enrolledAt;
    private LocalDateTime updatedAt;
}