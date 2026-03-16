package org.example.studyhub.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đăng ký"));

        User user = checkUserValid(dto.getEmail(), dto.getFullName());

        BeanUtils.copyProperties(dto, enrollment, "id", "updateAt", "enrolledAt");

        enrollment.setUser(user);
        enrollment.setCourse(courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại")));

        enrollment.setUpdatedAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }
    @Override
    @Transactional
    public void softDelete(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đăng ký để xóa!"));
        enrollment.setStatus("REJECTED");
        enrollment.setRejectedNotes("Khóa học này đã bị khóa lại bởi Manager hoặc Admin");
        enrollmentRepository.save(enrollment);
    }

    @Override
    public EnrollmentDTO getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đăng ký với ID: " + id));
        EnrollmentDTO dto = new EnrollmentDTO();
        BeanUtils.copyProperties(enrollment, dto);

        if (enrollment.getCourse() != null) {
            dto.setCourseId(enrollment.getCourse().getId());
        }
        return dto;
    }

    @Override
    @Transactional
    public void createEnrollment(EnrollmentDTO dto) {
        User user = checkUserValid(dto.getEmail(), dto.getFullName());

        Enrollment enrollment = new Enrollment();
        // Bỏ qua id, createdAt, enrolledAt để gán thủ công
        BeanUtils.copyProperties(dto, enrollment, "id", "updateAt", "enrolledAt");

        enrollment.setUser(user);
        enrollment.setCourse(courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại")));

        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setUpdatedAt(LocalDateTime.now());

        enrollmentRepository.save(enrollment);
    }

    @Override
    public Page<Enrollment> getMyEnrollments(Long userId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());
        return enrollmentRepository.findByUserId(userId, keyword, pageable);
    }


    @Override
    public Map<String, Object> prepareCheckoutData(Long courseId, Long enrollmentId, Long userId) {
        Map<String, Object> data = new HashMap<>();
        User user = userRepository.findById(userId).orElse(null);

        if (enrollmentId != null) {
            Enrollment e = enrollmentRepository.findById(enrollmentId).orElse(null);
            if (e != null) {
                data.put("courseTitle", e.getCourse().getTitle());
                data.put("fee", e.getFee());
                data.put("fullName", e.getFullName());
                data.put("mobile", e.getMobile());
                data.put("email", e.getEmail());
            }
        }
        return data;
    }
    @Override
    public ByteArrayInputStream exportEnrollmentsToExcel(Long courseId, String status, String keyword) {
        List<Enrollment> enrollments = enrollmentRepository.findAllWithFilter(courseId, status, keyword);

        System.out.println("Exporting size: " + enrollments.size());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Enrollments");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Khóa học", "Họ tên", "Email", "SĐT", "Học phí", "Ngày đăng ký", "Trạng thái"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (Enrollment e : enrollments) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getId());
                row.createCell(1).setCellValue(e.getCourse() != null ? e.getCourse().getTitle() : "");
                row.createCell(2).setCellValue(e.getFullName() != null ? e.getFullName() : "");
                row.createCell(3).setCellValue(e.getEmail() != null ? e.getEmail() : "");
                row.createCell(4).setCellValue(e.getMobile() != null ? e.getMobile() : "");
                row.createCell(5).setCellValue(e.getFee() != null ? e.getFee().doubleValue() : 0.0);
                row.createCell(6).setCellValue(e.getEnrolledAt() != null ? e.getEnrolledAt().toString() : "");
                row.createCell(7).setCellValue(e.getStatus() != null ? e.getStatus() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Lỗi xuất Excel: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void importEnrollments(MultipartFile file, Long courseId, Long adminId) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Enrollment> enrollmentsToSave = new ArrayList<>();

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
            User admin = userRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người quản lý"));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                String email = getCellValue(row.getCell(0)).trim();
                String statusFromExcel = getCellValue(row.getCell(1)).trim();

                if (email.isEmpty()) continue;

                User student = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException(" Email " + email + " chưa có tài khoản!"));
                Enrollment e = new Enrollment();
                BeanUtils.copyProperties(student, e);

                e.setId(null);
                e.setUser(student);

                e.setCourse(course);
                e.setStatus(statusFromExcel.toUpperCase());
                e.setFee(course.getListedPrice());
                e.setEnrolledAt(LocalDateTime.now());
                e.setUpdatedAt(LocalDateTime.now());
                e.setProgress(BigDecimal.ZERO);

                e.setEnrollNote("Imported by Admin: " + admin.getFullName() );

                enrollmentsToSave.add(e);
            }

            if (!enrollmentsToSave.isEmpty()) {
                enrollmentRepository.saveAll(enrollmentsToSave);
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi Import: " + e.getMessage());
        }
    }

    private User checkUserValid(String email, String fullName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lỗi: Email '" + email + "' chưa có tài khoản!"));

        if (!user.getFullName().equalsIgnoreCase(fullName)) {
            throw new RuntimeException("Lỗi: Họ tên '" + fullName + "' không khớp với email này!");
        }
        return user;
    }
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }


    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}