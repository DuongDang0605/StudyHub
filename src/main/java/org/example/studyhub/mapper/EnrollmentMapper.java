package org.example.studyhub.mapper;

import org.example.studyhub.dto.EnrollmentDTO;
import org.example.studyhub.model.Course;
import org.example.studyhub.model.Enrollment;
import org.example.studyhub.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EnrollmentMapper {

    public EnrollmentDTO toDTO(Enrollment enrollment) {
        if (enrollment == null) return null;

        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setFullName(enrollment.getFullName());
        dto.setEmail(enrollment.getEmail());
        dto.setMobile(enrollment.getMobile());
        dto.setFee(enrollment.getFee());
        dto.setPaymentMethod(enrollment.getPaymentMethod());
        dto.setEnrollNote(enrollment.getEnrollNote());
        dto.setStatus(enrollment.getStatus());
        dto.setRejectedNotes(enrollment.getRejectedNotes());
        dto.setProgress(enrollment.getProgress());
        dto.setCompletedAt(enrollment.getCompletedAt());
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        dto.setUpdatedAt(enrollment.getUpdatedAt());

        if (enrollment.getCourse() != null) {
            dto.setCourseId(enrollment.getCourse().getId());
            dto.setCourseTitle(enrollment.getCourse().getTitle());
        }

        if (enrollment.getUser() != null) {
            dto.setUserId(enrollment.getUser().getId());
        }

        return dto;
    }

    public Enrollment toEntity(EnrollmentDTO dto) {
        if (dto == null) return null;

        Enrollment enrollment = new Enrollment();
        enrollment.setFullName(dto.getFullName());
        enrollment.setEmail(dto.getEmail());
        enrollment.setMobile(dto.getMobile());
        enrollment.setFee(dto.getFee());
        enrollment.setPaymentMethod(dto.getPaymentMethod());
        enrollment.setEnrollNote(dto.getEnrollNote());
        enrollment.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
        enrollment.setRejectedNotes(dto.getRejectedNotes());
        enrollment.setProgress(dto.getProgress());

        if (dto.getCourseId() != null) {
            Course course = new Course();
            course.setId(dto.getCourseId());
            enrollment.setCourse(course);
        }

        if (dto.getUserId() != null) {
            User user = new User();
            user.setId(dto.getUserId());
            enrollment.setUser(user);
        }

        return enrollment;
    }

    public List<EnrollmentDTO> toDTOList(List<Enrollment> enrollments) {
        if (enrollments == null) return null;
        return enrollments.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Enrollment updateEntity(Enrollment existing, EnrollmentDTO dto) {
        if (existing == null || dto == null) return existing;

        if (dto.getFullName() != null) existing.setFullName(dto.getFullName());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getMobile() != null) existing.setMobile(dto.getMobile());
        if (dto.getFee() != null) existing.setFee(dto.getFee());
        if (dto.getPaymentMethod() != null) existing.setPaymentMethod(dto.getPaymentMethod());
        if (dto.getEnrollNote() != null) existing.setEnrollNote(dto.getEnrollNote());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getRejectedNotes() != null) existing.setRejectedNotes(dto.getRejectedNotes());
        if (dto.getProgress() != null) existing.setProgress(dto.getProgress());
        if (dto.getCompletedAt() != null) existing.setCompletedAt(dto.getCompletedAt());

        return existing;
    }
}