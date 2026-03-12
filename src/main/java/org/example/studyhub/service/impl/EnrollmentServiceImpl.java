package org.example.studyhub.service.impl;

import org.example.studyhub.dto.EnrollmentDTO;
import org.example.studyhub.model.Course;
import org.example.studyhub.model.Enrollment; // Thêm import model
import org.example.studyhub.model.User;
import org.example.studyhub.repository.CourseRepository;
import org.example.studyhub.repository.EnrollmentRepository;
import org.example.studyhub.repository.UserRepository;
import org.example.studyhub.service.EnrollmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page; // Sửa ở đây
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<Enrollment> getListEnrollmentPage(Long courseId, String status, String keyword, int page, int size, Long currentUserId, boolean isAdmin) {

        String statusParam = (status != null && !status.isEmpty() && !status.equals("All")) ? status : null;
        String keywordParam = (keyword != null && !keyword.isEmpty()) ? keyword : null;

        Long managerId = isAdmin ? null : currentUserId;

        Pageable pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());

        return enrollmentRepository.searchEnrollments(courseId, statusParam, keywordParam, managerId, pageable);
    }

    @Override
    @Transactional
    public void updateEnrollment(Long id, EnrollmentDTO dto) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu đăng ký"));

        BeanUtils.copyProperties(dto, enrollment, "id", "enrolledAt", "course", "user");
        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại"));
            enrollment.setCourse(course);
        }

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Người dùng sở hữu không tồn tại"));
            enrollment.setUser(user);
        }

        enrollment.setUpdatedAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đăng ký để xóa!"));
        enrollment.setStatus("REJECTED");
        enrollmentRepository.save(enrollment);
    }

    @Override
    public EnrollmentDTO getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đăng ký với ID: " + id));
        EnrollmentDTO dto = new EnrollmentDTO();
        BeanUtils.copyProperties(enrollment, dto);
        return dto;
    }

    @Override
    @Transactional
    public void createEnrollment(EnrollmentDTO dto) {
        Enrollment enrollment = new Enrollment();

        BeanUtils.copyProperties(dto, enrollment, "id");

        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            enrollment.setCourse(course);
        }
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setUpdatedAt(LocalDateTime.now());

        enrollmentRepository.save(enrollment);
    }

    @Override
    public Page<Enrollment> getMyEnrollments(Long userId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());
        return enrollmentRepository.findByUserId(userId, keyword, pageable);
    }

}