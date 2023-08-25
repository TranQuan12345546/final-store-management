package com.example.storemanagement.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendConfirmTokenResetPassword(String userEmail, String name, String resetLink) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(userEmail);
        helper.setSubject("Password Reset Request");

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("link", resetLink);

        String htmlContent = templateEngine.process("forgot-password-template", context);
        helper.setText(htmlContent, true);

        javaMailSender.send(message);

    }

    public void sendCheckInToken(String userEmail, String name, String token) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(userEmail);
        helper.setSubject("Checkin Token");

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("token", token);

        String htmlContent = templateEngine.process("check-in-template", context);
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    public void sendResisterCode(String email, String token) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("Mã xác nhận đăng kí ShopMefy");

        Context context = new Context();
        context.setVariable("token", token);

        String htmlContent = templateEngine.process("resister-template", context);
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }
}
