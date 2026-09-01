package com.contactsphere.services;

import org.springframework.web.multipart.MultipartFile;

public interface EmailService {

    void sendEmail(
        String to,
        String subject,
        String message,
        MultipartFile atttachment
    );

    void sendSimpleEmail(
        String to,
        String subject,
        String message
    );
}
