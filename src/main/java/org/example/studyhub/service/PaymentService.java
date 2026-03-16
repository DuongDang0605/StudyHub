package org.example.studyhub.service;

import org.example.studyhub.model.Course;
import org.example.studyhub.model.Enrollment;
import org.example.studyhub.repository.CourseRepository;
import org.example.studyhub.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

@Service
public class PaymentService {

    @Autowired
    private PayOS payOS;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public String createEnrollmentPaymentLink(Long enrollmentId) throws Exception {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đăng ký"));

        Course course = courseRepository.findByEnrollmentsId(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học "));


        long orderCode = System.currentTimeMillis() / 1000;
        enrollment.setOrderCode(orderCode);
        enrollment.setPaymentMethod("BANK_TRANSFER");
        enrollmentRepository.save(enrollment);

        String description = "Học phí khóa học " ;

        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(enrollment.getFee().longValue())
                .description(description)
                .returnUrl("http://localhost:8085/api/payment/success")
                .cancelUrl("http://localhost:8085/api/payment/cancel")
                .build();

        CreatePaymentLinkResponse response = payOS.paymentRequests().create(request);
        return response.getCheckoutUrl();
    }
}