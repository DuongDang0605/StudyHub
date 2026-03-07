package org.example.studyhub.service;

public interface EmailService {
    void sendNewAccountEmail(String toEmail, String username, String rawPassword);
}
