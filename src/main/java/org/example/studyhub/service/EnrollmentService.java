package org.example.studyhub.service;

import org.example.studyhub.model.Enrollment;
import org.springframework.data.domain.Page; // Sửa ở đây

public interface EnrollmentService {
    Page<Enrollment> getListEnrollmentPage(Long courseId, String status, String keyword, int page, int size);
}