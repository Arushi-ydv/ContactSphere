package com.contactsphere.controllers;

import com.contactsphere.entities.User;
import com.contactsphere.exceptions.ResourceNotFoundException;
import com.contactsphere.forms.ChangePasswordForm;
import com.contactsphere.repositories.UserRepo;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SettingsController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public SettingsController(UserRepo userRepo,
                              PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }

    @GetMapping("/settings/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "change-password";
    }

    @PostMapping("/settings/change-password")
    public String changePassword(
            @Valid @ModelAttribute("changePasswordForm") ChangePasswordForm changePasswordForm,
            BindingResult bindingResult,
            Model model, 
            Authentication authentication) {

        if(bindingResult.hasErrors()) {
            return "change-password";
        }

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(changePasswordForm.getCurrentPassword(), user.getPassword())) {
            model.addAttribute("error", "current");
            return "change-password";
        }

        if (!changePasswordForm.getNewPassword().equals(changePasswordForm.getConfirmPassword())) {
            model.addAttribute("error", "mismatch");
            return "change-password";
        }

        user.setPassword(passwordEncoder.encode(changePasswordForm.getNewPassword()));
        userRepo.save(user);

        return "redirect:/settings?passwordChanged=true";
    }
}