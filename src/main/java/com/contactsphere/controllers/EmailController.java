
package com.contactsphere.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactsphere.forms.EmailForm;
import com.contactsphere.services.EmailService;

import jakarta.validation.Valid;

@Controller
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-email")
    public String sendEmailPage(
            @RequestParam(required = false) String to,
            Model model) {

        EmailForm emailForm = new EmailForm();

        if (to != null && !to.isBlank()) {
            emailForm.setTo(to);
        }

        model.addAttribute("emailForm", emailForm);
        return "send-email";
    }

    @PostMapping("/email/send")
    public String sendEmail(
            @Valid @ModelAttribute("emailForm") EmailForm emailForm,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile attachment,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "send-email";
        }

        emailService.sendEmail(
                emailForm.getTo(),
                emailForm.getSubject(),
                emailForm.getMessage(),
                attachment
        );

        return "redirect:/send-email";
    }
}
