
package com.contactsphere.controllers;

import com.contactsphere.entities.User;
import com.contactsphere.entities.VerificationToken;
import com.contactsphere.entities.Providers;

import com.contactsphere.forms.UserForm;

import com.contactsphere.repositories.UserRepo;
import com.contactsphere.repositories.VerificationTokenRepo;

import com.contactsphere.services.EmailService;
import com.contactsphere.services.UserService;
import com.contactsphere.services.PasswordResetService;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final UserService userService;
    private final UserRepo userRepo;
    private final VerificationTokenRepo verificationTokenRepo;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;

    public PageController(
            UserService userService,
            UserRepo userRepo,
            VerificationTokenRepo verificationTokenRepo,
            EmailService emailService,
            PasswordResetService passwordResetService) {

        this.userService = userService;
        this.userRepo = userRepo;
        this.verificationTokenRepo = verificationTokenRepo;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
    }

    @Value("${app.base-url}")
    private String appBaseUrl;

    @ModelAttribute
    public void addAuthentication(Model model, Authentication authentication) {

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            model.addAttribute("loggedInUser", authentication.getName());
        }
    }

    @RequestMapping("/home")
    public String home(Model model) {

        System.out.println("Home page handler");

        model.addAttribute("name", "Substring Technologies");
        model.addAttribute("youtubeChannel", "Learn Code");
        model.addAttribute("githubRepo", "https://github.com/learncodewithdurgesh/");

        return "home";
    }

    @RequestMapping("/about")
    public String aboutPage(Model model) {

        model.addAttribute("isLogin", true);
        System.out.println("About page loading");

        return "about";
    }

    @RequestMapping("/services")
    public String servicesPage() {

        System.out.println("services page loading");

        return "services";
    }

    @GetMapping("/contact")
    public String contact() {

        return "contact";
    }

    @GetMapping("/login")
    public String login() {

        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {

        UserForm userForm = new UserForm();

        model.addAttribute("userForm", userForm);

        return "register";
    }

    // Process Registration
    @PostMapping("/do-register")
    public String processRegister(
            @Valid @ModelAttribute("userForm") UserForm userForm,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userRepo.existsByEmail(userForm.getEmail())) {

            bindingResult.rejectValue(
                    "email",
                    "error.userForm",
                    "Email is already registered"
            );

            return "register";
        }

        User user = new User();

        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());

        User savedUser = userService.saveUser(user);

        // Generate verification token
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setToken(token);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        verificationTokenRepo.save(verificationToken);

        String verificationLink =
                appBaseUrl + "/verify-email?token=" + token;

        emailService.sendSimpleEmail(
                savedUser.getEmail(),
                "Verify Your ContactSphere Account",
                "Click the link below to verify your email:\n\n"
                        + verificationLink
        );

        return "redirect:/login?registered=true";
    }

    // Resend Verification Email
    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email) {

        User user = userRepo.findByEmail(email).orElse(null);

        if (user == null
                || user.isEmailVerified()
                || user.getProvider() != Providers.SELF) {

            return "redirect:/login?resend=requested";
        }

        verificationTokenRepo.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        verificationTokenRepo.save(verificationToken);

        String verificationLink =
                appBaseUrl + "/verify-email?token=" + token;

        emailService.sendSimpleEmail(
                user.getEmail(),
                "Verify Your ContactSphere Account",
                "Click the link below to verify your email:\n\n"
                        + verificationLink
        );

        return "redirect:/login?resend=requested";
    }

    // Verify Email
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {

        VerificationToken verificationToken = verificationTokenRepo
                .findByToken(token)
                .orElse(null);

        if (verificationToken == null) {
            return "redirect:/login?error=invalid-token";
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "redirect:/login?error=expired-token";
        }

        User user = verificationToken.getUser();

        user.setEmailVerified(true);
        user.setEnabled(true);

        userRepo.save(user);

        verificationTokenRepo.deleteByUser(user);

        return "redirect:/login?verified=true";
    }

    // ==========================================
    // FORGOT PASSWORD
    // ==========================================

    // Open Forgot Password Page
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {

        return "forgot-password";
    }

    // Process Forgot Password Request
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email) {

        // Delegate business logic to service
        // Generic response prevents account enumeration
        passwordResetService.sendResetLink(email);

        return "redirect:/forgot-password?sent=true";
    }

    // ==========================================
    // RESET PASSWORD
    // ==========================================

    // Open Reset Password Page
    @GetMapping("/reset-password")
    public String resetPasswordPage(
            @RequestParam String token,
            Model model) {

        if (!passwordResetService.isValidResetToken(token)) {
            return "redirect:/login?reset=invalid";
        }

        model.addAttribute("token", token);

        return "reset-password";
    }

    // Process New Password
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        // Validate reset token
        if (!passwordResetService.isValidResetToken(token)) {
            return "redirect:/login?reset=invalid";
        }

        // Validate password length
        if (newPassword == null || newPassword.length() < 8) {

            model.addAttribute("token", token);
            model.addAttribute(
                    "error",
                    "Password must contain at least 8 characters."
            );

            return "reset-password";
        }

        // Confirm password
        if (!newPassword.equals(confirmPassword)) {

            model.addAttribute("token", token);
            model.addAttribute("error", "Passwords do not match.");

            return "reset-password";
        }

        // Delegate password update to service
        boolean resetSuccessful = passwordResetService.resetPassword(
                token,
                newPassword,
                confirmPassword
        );

        if (!resetSuccessful) {
            return "redirect:/login?reset=invalid";
        }

        return "redirect:/login?password-reset=success";
    }
}
