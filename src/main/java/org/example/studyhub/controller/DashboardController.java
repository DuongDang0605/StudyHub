package org.example.studyhub.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.studyhub.annotation.RequireRole;
import org.example.studyhub.dto.ChapterDTO;
import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.model.User;
import org.example.studyhub.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@RequireRole({"Admin","Manager"})
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public String showDashboard(Model model, HttpSession session,
                                @RequestParam(defaultValue = "0") int page) {
        User currentUser = (User) session.getAttribute("loggedInUser");

        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        String username = currentUser.getUsername();
        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equalsIgnoreCase("Admin"));

        model.addAllAttributes(dashboardService.getDashboardData(username, isAdmin, page));
        return "admin/dashboard/dashboard";
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", dashboardService.getCourseDtoById(id));
        model.addAttribute("categories", dashboardService.getAllCategories());
        model.addAttribute("instructors", dashboardService.getAllInstructors());

        return "admin/dashboard/course-edit";
    }

    @GetMapping("/{id}/chapters")
    public String showChapters(@PathVariable Long id, Model model) {
        CourseDTO course = dashboardService.getCourseDtoById(id);
        List<ChapterDTO> chapters = dashboardService.getChaptersByCourseId(id);

        model.addAttribute("course", course);
        model.addAttribute("chapters", chapters);
        return "admin/dashboard/chapter-list";
    }
    @PostMapping("/update")
    public String updateCourse(@ModelAttribute("course") CourseDTO courseDto, RedirectAttributes ra) {
        try {
            dashboardService.updateCourseFromDto(courseDto);
            ra.addFlashAttribute("message", "Cập nhật khóa học thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin";
    }
}
