package org.example.studyhub.service.impl;

import jakarta.transaction.Transactional;
import org.example.studyhub.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendNewAccountEmail(String toEmail, String username, String rawPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Thông tin tài khoản StudyHub");

        String mailContent = "Chào bạn,\n\n"
                + "Tài khoản của bạn đã được tạo thành công trên hệ thống StudyHub.\n"
                + "Tên đăng nhập: " + username + "\n"
                + "Mật khẩu tạm thời: " + rawPassword + "\n\n"
                + "Vui lòng đăng nhập và đổi mật khẩu ngay sau khi nhận được email này để đảm bảo bảo mật.\n\n"
                + "Trân trọng,\nĐội ngũ StudyHub";

        message.setText(mailContent);
        mailSender.send(message);
    }

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Xác thực tài khoản StudyHub của bạn");

        String verifyLink = "http://localhost:8085/auth/verify?token=" + token;

        String mailContent = "Chào bạn,\n\n"
                + "Vui lòng click vào đường link bên dưới để xác thực tài khoản của bạn:\n"
                + verifyLink + "\n\n"
                + "Link này sẽ hết hạn sau 24 giờ.\n\n"
                + "Trân trọng,\nĐội ngũ StudyHub";

        message.setText(mailContent);
        mailSender.send(message);
    }
}
