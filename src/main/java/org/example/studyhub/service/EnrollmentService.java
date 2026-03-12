package org.example.studyhub.service;

import org.example.studyhub.dto.EnrollmentDTO;
import org.example.studyhub.model.Enrollment;
import org.springframework.data.domain.Page;

public interface EnrollmentService {
    Page<Enrollment> getListEnrollmentPage(Long courseId, String status, String keyword, int page, int size, Long userId, boolean isAdmin);
    void updateEnrollment(Long id, EnrollmentDTO dto);
    void softDelete(Long id);
    EnrollmentDTO getEnrollmentById(Long id);
    void createEnrollment(EnrollmentDTO dto);
    Page<Enrollment> getMyEnrollments(Long userId, String keyword, int page, int size);
}