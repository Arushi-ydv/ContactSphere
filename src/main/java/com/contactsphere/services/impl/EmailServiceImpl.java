package com.contactsphere.services.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.contactsphere.services.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(
            String to,
            String subject,
            String message,
            MultipartFile attachment) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message);

            if(attachment != null && !attachment.isEmpty()) {

                String fileName = attachment.getOriginalFilename();

                if(fileName == null || fileName.contains("..")) {
                    throw new IllegalArgumentException("Invalid file name");
                }

                if(attachment.getSize() > 5 * 1024 * 1024) {
                    throw new IllegalArgumentException("Attachment size must be less than 5 MB");
                }

                String contentType = attachment.getContentType();

                if (contentType == null ||
                        !(contentType.equals("application/pdf")
                        || contentType.equals("image/jpeg")
                        || contentType.equals("image/png")
                        || contentType.equals("text/plain")
                        || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                        || contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {

                    throw new IllegalArgumentException("Unsupported attachment type");
                }

                helper.addAttachment(fileName, attachment);
            }
            mailSender.send(mimeMessage);
            
        } catch(MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        }       
    }

    @Override
    public void sendSimpleEmail(
            String to,
            String subject,
            String message) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        }       
    }
}
