package org.example.studyhub.controller;

import jakarta.servlet.http.HttpSession;
import org.example.studyhub.model.Enrollment;
import org.example.studyhub.model.User;
import org.example.studyhub.service.CourseService;
import org.example.studyhub.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;

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
}

