package org.example.studyhub.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.studyhub.dto.EnrollmentDTO;
import org.example.studyhub.model.Enrollment;
import org.example.studyhub.model.User;
import org.example.studyhub.service.CourseService;
import org.example.studyhub.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/enrollments")
public class EnrollmentAdminController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;

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
                .anyMatch(ur -> ur.getRole().getName().equalsIgnoreCase("ADMIN"));


        Page<Enrollment> enrollmentPage = enrollmentService.getListEnrollmentPage(
                courseId, status, keyword, page, 10, currentUser.getId(), isAdmin);

        model.addAttribute("enrollments", enrollmentPage.getContent());
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUserId", currentUser.getId());


        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", enrollmentPage.getTotalPages());
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);

        return "admin/enrollment/enrollment-list";
    }

    @GetMapping("/edit/{id}")

    public String showEditForm(@PathVariable Long id, Model model) {

        EnrollmentDTO enrollmentDTO = enrollmentService.getEnrollmentById(id);

        model.addAttribute("activeCourses", courseService.getActiveCoursesByStatus());

        model.addAttribute("enrollmentDTO", enrollmentDTO);

        return "admin/enrollment/enrollment-form";

    }


    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("enrollmentDTO") EnrollmentDTO dto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activeCourses", courseService.getActiveCoursesByStatus());
            return "admin/enrollment/enrollment-form";
        }
        try {
            enrollmentService.updateEnrollment(id, dto);
            ra.addFlashAttribute("message", "Cập nhật thành công!");
            return "redirect:/admin/enrollments";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("activeCourses", courseService.getActiveCoursesByStatus());
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
                         BindingResult result,
                         Model model,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activeCourses", courseService.getActiveCoursesByStatus());
            return "admin/enrollment/enrollment-form";
        }
        try {
            enrollmentService.createEnrollment(dto);
            ra.addFlashAttribute("message", "Thêm mới thành công!");
            return "redirect:/admin/enrollments";
        } catch (Exception e) {

            model.addAttribute("error", "Lỗi: " + e.getMessage());

            model.addAttribute("activeCourses", courseService.getActiveCoursesByStatus());

            return "admin/enrollment/enrollment-form";
        }
    }
}

