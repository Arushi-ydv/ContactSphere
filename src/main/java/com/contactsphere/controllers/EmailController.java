package com.contactsphere.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactsphere.services.EmailService;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send-email")
    public String sendEmailPage() {
        return "send-email";
    }

    @PostMapping("/email/send")
    public String sendEmail(
        @RequestParam String to,
        @RequestParam String subject,
        @RequestParam String message,
        @RequestParam(required = false) MultipartFile attachment) {

            emailService.sendEmail(to, subject, message, attachment);

            return "redirect:/send-email";
    }
}
