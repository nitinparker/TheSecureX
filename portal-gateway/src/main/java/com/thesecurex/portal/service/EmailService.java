package com.thesecurex.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendInviteEmail(String toEmail, String code, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Invitation to TheSecureX");
        message.setText("Hello " + name + ",\n\n" +
                "Your request for access has been approved by the OEM Administrator.\n" +
                "You have been granted access to specific security modules.\n\n" +
                "Here is your invitation code: " + code + "\n\n" +
                "Please use this code to sign up at: http://localhost:8081/signup\n" +
                "Note: This code will expire based on the validity period set by the administrator.\n\n" +
                "Best regards,\nTheSecureX Team");

        mailSender.send(message);
    }
}
