package org.example.studyhub.controller;

import jakarta.servlet.http.HttpSession;
import org.example.studyhub.model.User;
import org.example.studyhub.repository.EnrollmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MyCourseController {

    private final EnrollmentRepository enrollmentRepository;

    public MyCourseController(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping("/my-courses")
    public String myCourses(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "6") int size,
                            @RequestParam(required = false) String keyword,
                            HttpSession session,
                            Model model) {

        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        @SuppressWarnings("unchecked")
        List<String> roleNames = (List<String>) session.getAttribute("roleNames");
        boolean allowed = roleNames != null && roleNames.stream().anyMatch(role ->
                "admin".equalsIgnoreCase(role)
                        || "manager".equalsIgnoreCase(role)
                        || "member".equalsIgnoreCase(role));

        if (!allowed) {
            return "redirect:/home";
        }

        String keywordPattern = (keyword == null || keyword.isBlank())
                ? null
                : "%" + keyword.trim().toLowerCase() + "%";
        boolean isAdmin = roleNames != null && roleNames.stream().anyMatch(r -> "admin".equalsIgnoreCase(r));
        boolean isManager = roleNames != null && roleNames.stream().anyMatch(r -> "manager".equalsIgnoreCase(r));

        Page<?> myCoursePage;
        var pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());
        if (isAdmin) {
            myCoursePage = enrollmentRepository.findAllApproved(keywordPattern, pageable);
        } else if (isManager) {
            myCoursePage = enrollmentRepository.findApprovedByInstructor(currentUser.getId(), keywordPattern, pageable);
        } else {
            myCoursePage = enrollmentRepository.findApprovedByUserId(
                    currentUser.getId(),
                    currentUser.getEmail(),
                    keywordPattern,
                    pageable
            );
        }

        model.addAttribute("myCourses", myCoursePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", myCoursePage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);

        return "homepage/course/my-courses";
    }
}
