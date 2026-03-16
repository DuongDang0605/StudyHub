package org.example.studyhub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.example.studyhub.model.Enrollment;
import org.example.studyhub.repository.EnrollmentRepository;
import org.example.studyhub.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private PayOS payOS;

    @Autowired
    private ObjectMapper objectMapper;


    @GetMapping("/pay/{id}")
    public void redirectToPayOS(@PathVariable Long id, HttpServletResponse response) throws IOException {
        try {
            String checkoutUrl = paymentService.createEnrollmentPaymentLink(id);
            response.sendRedirect(checkoutUrl);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("/course/my-enrollments?error=payment_failed");
        }
    }

    @PostMapping("/create-link/{id}")
    public ResponseEntity<String> createLink(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(paymentService.createEnrollmentPaymentLink(id));
    }

    @PostMapping("/payos-webhook")
    public ResponseEntity<?> handlePayosWebhook(@RequestBody Map<String, Object> body) {
        try {
            var verifiedData = payOS.webhooks().verify(body);

            Long orderCode = verifiedData.getOrderCode();
            Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByOrderCode(orderCode);

            if (enrollmentOpt.isPresent()) {
                Enrollment enrollment = enrollmentOpt.get();
                if (!"APPROVED".equals(enrollment.getStatus())) {
                    enrollment.setStatus("APPROVED");
                    enrollment.setUpdatedAt(LocalDateTime.now());
                    enrollmentRepository.save(enrollment);
                }
                return ResponseEntity.ok("Xác thực và duyệt thành công.");
            }
            return ResponseEntity.status(404).body("Không tìm thấy đơn hàng.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi xác thực: " + e.getMessage());
        }
    }
//    @PostConstruct
//    public void registerWebhook() {
//        try {
//            payOS.webhooks().confirm("https://2c1e-14-191-32-164.ngrok-free.app/api/payment/payos-webhook");
//            System.out.println("Đăng ký webhook thành công");
//        } catch (Exception e) {
//            System.out.println("Lỗi đăng ký webhook: " + e.getMessage());
//        }
//    }

    @GetMapping("/success")
    public void paymentSuccess(@RequestParam(required = false) String code,
                               @RequestParam(required = false) Long orderCode,
                               HttpServletResponse response) throws IOException {
        if ("00".equals(code) && orderCode != null) {
            Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByOrderCode(orderCode);
            if (enrollmentOpt.isPresent()) {
                Enrollment enrollment = enrollmentOpt.get();
                if (!"APPROVED".equals(enrollment.getStatus())) {
                    enrollment.setStatus("APPROVED");
                    enrollment.setUpdatedAt(LocalDateTime.now());
                    enrollmentRepository.save(enrollment);
                }
            }
        }
        response.sendRedirect("/enroll/my-list");
    }

    @GetMapping("/cancel")
    public void paymentCancel(@RequestParam(required = false) Long orderCode,
                              HttpServletResponse response) throws IOException {
        if (orderCode != null) {
            Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByOrderCode(orderCode);
            enrollmentOpt.ifPresent(enrollment -> {
                enrollment.setStatus("CANCELLED");
                enrollment.setEnrollNote("Hủy Thanh Toán ");
                enrollment.setUpdatedAt(LocalDateTime.now());
                enrollmentRepository.save(enrollment);
            });
        }
        response.sendRedirect("/enroll/my-list");
    }
}