package org.example.studyhub.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.studyhub.dto.EnrollmentDTO;
import org.example.studyhub.model.Course;
import org.example.studyhub.model.Enrollment;
import org.example.studyhub.model.User;
import org.example.studyhub.service.CourseService;
import org.example.studyhub.service.EnrollmentService;
import org.example.studyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/enrollments")
public class EnrollmentAdminController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String listEnrollments(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) return "redirect:/auth/login";

        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equalsIgnoreCase("Admin"));


        List<Course> coursesList;
        if (isAdmin) {
            coursesList = courseService.getAllCourses();
        } else {
            coursesList = courseService.getCoursesByInstructor(currentUser.getId());
        }
        model.addAttribute("courses", coursesList);


        Page<Enrollment> enrollmentPage = enrollmentService.getListEnrollmentPage(
                courseId, status, keyword, page, 10, currentUser.getId(), isAdmin);

        model.addAttribute("enrollmentPage", enrollmentPage);
        model.addAttribute("enrollments", enrollmentPage.getContent());
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", enrollmentPage.getTotalPages());
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);

        return "admin/enrollment/enrollment-list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) return "redirect:/auth/login";
        EnrollmentDTO enrollmentDTO = enrollmentService.getEnrollmentById(id);
        model.addAttribute("enrollmentDTO", enrollmentDTO);

        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equalsIgnoreCase("Admin"));

        List<Course> courses;
        if (isAdmin) {
            courses = courseService.getAllCourses();
        } else {
            courses = courseService.getCoursesByInstructor(currentUser.getId());
        }

        model.addAttribute("activeCourses", courses);

        return "admin/enrollment/enrollment-form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("enrollmentDTO") EnrollmentDTO dto,
                         BindingResult result, Model model, HttpSession session, RedirectAttributes ra) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equalsIgnoreCase("Admin"));

        if (result.hasErrors()) {
            model.addAttribute("activeCourses", isAdmin ? courseService.getAllCourses() : courseService.getCoursesByInstructor(currentUser.getId()));
            return "admin/enrollment/enrollment-form";
        }
        try {
            enrollmentService.updateEnrollment(id, dto);
            ra.addFlashAttribute("message", "Cập nhật thành công!");
            return "redirect:/admin/enrollments";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("activeCourses", isAdmin ? courseService.getAllCourses() : courseService.getCoursesByInstructor(currentUser.getId()));
            return "admin/enrollment/enrollment-form";
        }
    }

    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            enrollmentService.softDelete(id);
            ra.addFlashAttribute("message", "Đã hủy đơn đăng ký thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/admin/enrollments";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        EnrollmentDTO dto = EnrollmentDTO.builder()
                .status("PENDING")
                .fee(BigDecimal.ZERO)
                .progress(BigDecimal.ZERO)
                .build();
        model.addAttribute("enrollmentDTO", dto);
        model.addAttribute("activeCourses", courseService.getActiveCoursesByStatus());
        return "admin/enrollment/enrollment-form";
    }


    @PostMapping("/add")
    public String create(@Valid @ModelAttribute("enrollmentDTO") EnrollmentDTO dto,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activeCourses", courseService.getActiveCoursesByStatus());
            return "admin/enrollment/enrollment-form";
        }
        try {
            enrollmentService.createEnrollment(dto);
            ra.addFlashAttribute("message", "Thêm mới thành công!");
            return "redirect:/admin/enrollments";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage()); // Truyền thông điệp lỗi sang HTML
            model.addAttribute("activeCourses", courseService.getActiveCoursesByStatus());
            return "admin/enrollment/enrollment-form";
        }
    }


    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportEnrollments(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {

        ByteArrayInputStream in = enrollmentService.exportEnrollmentsToExcel(courseId, status, keyword);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=danh-sach-dang-ky.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/import-page")
    public String showImportPage(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) return "redirect:/auth/login";

        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equalsIgnoreCase("Admin"));

        List<Course> coursesList;

        if (isAdmin) {
            List<User> managers = userService.getUsersByRole("Manager");
            model.addAttribute("admins", managers);
            model.addAttribute("isOnlyOneManager", false);

            coursesList = courseService.getAllCourses();
        } else {
            model.addAttribute("fixedManager", currentUser);
            model.addAttribute("admins", List.of(currentUser));
            model.addAttribute("isOnlyOneManager", true);

            coursesList = courseService.getCoursesByInstructor(currentUser.getId());
        }

        model.addAttribute("courses", coursesList);

        return "admin/enrollment/enrollment-import";
    }

    @PostMapping("/import")
    public String handleImportExcel(@RequestParam("file") MultipartFile file,
                                    @RequestParam("courseId") Long courseId,
                                    @RequestParam("adminId") Long adminId,
                                    RedirectAttributes ra) {
        if (file.isEmpty()) {
            ra.addFlashAttribute("error", "Vui lòng chọn một file Excel để import!");
            return "redirect:/admin/enrollments/import-page";
        }

        try {
            enrollmentService.importEnrollments(file, courseId, adminId);
            ra.addFlashAttribute("message", "Import danh sách đăng ký thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi import: " + e.getMessage());
            return "redirect:/admin/enrollments/import-page";
        }

        return "redirect:/admin/enrollments";
    }
}

