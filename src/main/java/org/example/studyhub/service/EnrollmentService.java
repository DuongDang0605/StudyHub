package org.example.studyhub.service;

import org.example.studyhub.dto.EnrollmentDTO;
import org.example.studyhub.model.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

import java.util.Map;

public interface EnrollmentService {
    Page<Enrollment> getListEnrollmentPage(Long courseId, String status, String keyword, int page, int size, Long userId, boolean isAdmin);
    void updateEnrollment(Long id, EnrollmentDTO dto);
    void softDelete(Long id);
    EnrollmentDTO getEnrollmentById(Long id);
    void createEnrollment(EnrollmentDTO dto);
    Page<Enrollment> getMyEnrollments(Long userId, String keyword, int page, int size);


    Map<String, Object> prepareCheckoutData(Long courseId, Long enrollmentId, Long userId);

    ByteArrayInputStream exportEnrollmentsToExcel(Long courseId, String status, String keyword);
    void importEnrollments(MultipartFile file, Long courseId, Long adminId);
}