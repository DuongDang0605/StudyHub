package org.example.studyhub.service.impl;

import org.example.studyhub.model.Enrollment; // Thêm import model
import org.example.studyhub.repository.EnrollmentRepository;
import org.example.studyhub.service.EnrollmentService;
import org.springframework.data.domain.Page; // Sửa ở đây
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public Page<Enrollment> getListEnrollmentPage(Long courseId, String status, String keyword, int page, int size, Long currentUserId, boolean isAdmin) {

        String statusParam = (status != null && !status.isEmpty() && !status.equals("All")) ? status : null;
        String keywordParam = (keyword != null && !keyword.isEmpty()) ? keyword : null;

        Long managerId = isAdmin ? null : currentUserId;

        Pageable pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());

        return enrollmentRepository.searchEnrollments(courseId, statusParam, keywordParam, managerId, pageable);
    }
}