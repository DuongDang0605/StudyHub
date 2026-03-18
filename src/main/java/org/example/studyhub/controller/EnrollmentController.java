package org.example.studyhub.controller;

import jakarta.servlet.http.HttpSession;
import org.example.studyhub.dto.EnrollmentDTO;
import org.example.studyhub.dto.UserDTO;
import org.example.studyhub.model.Course;
import org.example.studyhub.model.Enrollment;
import org.example.studyhub.model.User;
import org.example.studyhub.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.Map;

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
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

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

    @GetMapping("/pay-checkout")
    public String showCheckout(
            @RequestParam(value = "courseId", required = false) Long courseId,
            @RequestParam(value = "enrollmentId", required = false) Long enrollmentId,
            Model model, HttpSession session) {
        model.addAttribute("enrollmentId", enrollmentId);
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) return "redirect:/auth/login";

        Map<String, Object> checkoutData = enrollmentService.prepareCheckoutData(courseId, enrollmentId, currentUser.getId());


        model.addAllAttributes(checkoutData);

        return "homepage/enrollment/checkout";
    }
    @PostMapping("/create-and-checkout")
    public String createAndCheckout(@RequestParam Long courseId, HttpSession session) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            Long enrollmentId = enrollmentService.createEnrollmentAndGetId(courseId, currentUser.getId());
            return "redirect:/enroll/pay-checkout?courseId=" + courseId + "&enrollmentId=" + enrollmentId;
        } catch (Exception e) {
            return "redirect:/admin/courses?error=" + e.getMessage();
        }
    }
}
