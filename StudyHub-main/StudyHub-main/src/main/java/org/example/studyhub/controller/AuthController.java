package org.example.studyhub.controller;

import jakarta.servlet.http.HttpSession;
import org.example.studyhub.model.User;
import org.example.studyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm(HttpSession session, Model model) {
        String interceptedError = (String) session.getAttribute("interceptedError");
        if (interceptedError != null) {
            model.addAttribute("error", interceptedError);
            session.removeAttribute("interceptedError");
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        User user = userService.authenticate(email, password);

        if (user == null) {
            model.addAttribute("error", "Sai email hoặc mật khẩu.");
            return "auth/login";
        }

        if ("INACTIVE".equals(user.getStatus())) {
            model.addAttribute("error", "Tài khoản của bạn đã bị vô hiệu hóa.");
            return "auth/login";
        }

        if ("UNVERIFIED".equals(user.getStatus())) {
            model.addAttribute("unverifiedError", "Tài khoản của bạn chưa được xác thực.");
            model.addAttribute("unverifiedEmail", user.getEmail());
            return "auth/login";
        }

        session.setAttribute("loggedInUser", user);
        return "redirect:/home";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            userService.resendVerificationEmail(email);
            redirectAttributes.addFlashAttribute("successMessage", "Link xác thực đã được gửi lại vào email của bạn.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            userService.resendVerificationEmail(email);
            redirectAttributes.addFlashAttribute("successMessage", "Email khôi phục mật khẩu đã được gửi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài khoản hợp lệ với email này.");
        }
        return "redirect:/auth/forgot-password";
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {
        boolean isValid = userService.validateVerificationToken(token);
        if (!isValid) {
            redirectAttributes.addFlashAttribute("errorMessage", "Link không hợp lệ hoặc đã hết hạn.");
            return "redirect:/auth/login";
        }
        model.addAttribute("token", token);
        return "auth/setup-password";
    }

    @PostMapping("/setup-password")
    public String setupNewPassword(@RequestParam("token") String token,
                                   @RequestParam("newPassword") String newPassword,
                                   @RequestParam("confirmPassword") String confirmPassword,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            model.addAttribute("token", token);
            return "auth/setup-password";
        }
        try {
            userService.verifyAndSetupPassword(token, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công! Vui lòng đăng nhập.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "auth/setup-password";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}