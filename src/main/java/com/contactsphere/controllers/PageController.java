package com.contactsphere.controllers;

import com.contactsphere.entities.User;
import com.contactsphere.entities.VerificationToken;
import com.contactsphere.forms.UserForm;
import com.contactsphere.repositories.UserRepo;
import com.contactsphere.repositories.VerificationTokenRepo;
import com.contactsphere.services.EmailService;
import com.contactsphere.services.UserService;

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

    public PageController(
            UserService userService,
            UserRepo userRepo,
            VerificationTokenRepo verificationTokenRepo,
            EmailService emailService) {

        this.userService = userService;
        this.userRepo = userRepo;
        this.verificationTokenRepo = verificationTokenRepo;
        this.emailService = emailService;
    }

    @Value("${app.base-url}")
    private String appBaseUrl;
 
    @ModelAttribute
    public void addAuthentication(Model model, Authentication authentication) {
        if(authentication != null 
            && authentication.isAuthenticated() 
            && !(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("loggedInUser", authentication.getName());
        }
    }

    @RequestMapping("/home")
    public String home(Model model) {
        System.out.println("Home page handler");

        // sending data to view
        model.addAttribute("name", "Substring Technologies");
        model.addAttribute("youtubeChannel", "Learn Code");
        model.addAttribute("githubRepo", "https://github.com/learncodewithdurgesh/");
        return "home";
    }

    // about route

    @RequestMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin", true);
        System.out.println("About page loading");
        return "about";
    }

    //services
    
    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("services page loading");
        return "services";
    }

    // contact page
    @GetMapping("/contact")
    public String contact() {
        return new String("contact");
    }

    // login
    @GetMapping("/login")
    public String login() {
        return new String("login");
    }

    // register
    @GetMapping("/register")
    public String register(Model model) {
        UserForm userForm = new UserForm();
        // userForm.setName("Arushi");
        // userForm.setAbout("This is about : Write something about yourself");
        model.addAttribute("userForm", userForm);
        return "register";
    }
    
    //Processing register
    @PostMapping("/do-register")
    public String processRegister(
            @Valid @ModelAttribute("userForm") UserForm userForm,
            BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "register";
        }

        if(userRepo.existsByEmail(userForm.getEmail())) {
            
            bindingResult.rejectValue(
                "email",
                "error.userForm",
                "Email is already registered"
            );

            return "register";
        }

        // create user
        User user = new User();

        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());

        // save user
        User savedUser = userService.saveUser(user);

        // Generate verification token
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setToken(token);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        verificationTokenRepo.save(verificationToken);

        String verificationLink = appBaseUrl + "/verify-email?token=" + token;

        emailService.sendSimpleEmail(savedUser.getEmail(), "Verify Your ContactSphere Account", "Click the link below to verify your email:\n\n" + verificationLink);

        // Redirect to login page
        return "redirect:/login?registered=true";
    }

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
}
