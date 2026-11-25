
package com.contactsphere.controllers;

import com.contactsphere.entities.User;
import com.contactsphere.exceptions.ResourceNotFoundException;
import com.contactsphere.forms.ProfileForm;
import com.contactsphere.repositories.UserRepo;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    private final UserRepo userRepo;

    public ProfileController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProfileForm profileForm = new ProfileForm();
        profileForm.setName(user.getName());
        profileForm.setPhoneNumber(user.getPhoneNumber());
        profileForm.setAbout(user.getAbout());

        model.addAttribute("user", user);
        model.addAttribute("profileForm", profileForm);

        return "edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute ProfileForm profileForm,
            BindingResult bindingResult,
            Authentication authentication,
            Model model) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "edit-profile";
        }

        user.setName(profileForm.getName());
        user.setPhoneNumber(profileForm.getPhoneNumber());
        user.setAbout(profileForm.getAbout());

        userRepo.save(user);

        return "redirect:/profile";
    }
}
