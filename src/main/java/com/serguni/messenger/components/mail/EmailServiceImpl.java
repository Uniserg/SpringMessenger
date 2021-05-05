package com.serguni.messenger.components.mail;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender = new MailProperties().getJavaMailSender();

    private final static String MAIL_PUBLIC = System.getenv("MAIL_PUBLIC");

    public EmailServiceImpl() {
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        new Thread(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(MAIL_PUBLIC);
            message.setTo(to);
            message.setText(text);
            message.setSubject(subject);
            emailSender.send(message);
        }).start();
    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text,String pathToAttachment) {
        new Thread(() -> {
            try {
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(MAIL_PUBLIC);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(text);

                FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
                helper.addAttachment("Invoice", file);

                emailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }
}