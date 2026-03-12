package org.example.studyhub.controller;

import jakarta.servlet.http.HttpSession;
import org.example.studyhub.dto.EnrollmentDTO;
import org.example.studyhub.dto.UserDTO;
import org.example.studyhub.model.Enrollment;
import org.example.studyhub.model.User;
import org.example.studyhub.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
@Controller
@RequestMapping("/enroll")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;
    @GetMapping("/my-list")
    public String myEnrollments(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) String keyword,
                                HttpSession session) {

        User currentUser = (User) session.getAttribute("loggedInUser");

        Page<Enrollment> enrollmentPage = enrollmentService.getMyEnrollments(
                currentUser.getId(), keyword, page, 10);

        model.addAttribute("enrollments", enrollmentPage);
        model.addAttribute("keyword", keyword);
        return "homepage/enrollment/my-enrollments";
    }


    @GetMapping("/details/{id}")
    @ResponseBody
    public EnrollmentDTO getDetails(@PathVariable Long id) {
        return enrollmentService.getEnrollmentById(id);
    }
}
