package com.contactsphere.controllers;

import com.contactsphere.entities.Contact;
import com.contactsphere.entities.User;
import com.contactsphere.entities.VerificationToken;
import com.contactsphere.forms.UserForm;
import com.contactsphere.repositories.UserRepo;
import com.contactsphere.repositories.VerificationTokenRepo;
import com.contactsphere.services.CloudinaryService;
import com.contactsphere.services.ContactService;
import com.contactsphere.services.EmailService;
import com.contactsphere.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepo verificationTokenRepo;

    @Autowired
    private EmailService emailService;

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
    
    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Contact> contacts = contactService.getAllContacts(user);
        long totalContacts = contacts.size();

        long favouriteContacts = contacts.stream()
                .filter(Contact::isFavourite)
                .count();

        long socialLinks = contacts.stream()
                .mapToLong(contact -> contact.getLinks().size())
                .sum();

        model.addAttribute("user", user);
        model.addAttribute("contacts", contacts);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favouriteContacts", favouriteContacts);
        model.addAttribute("socialLinks", socialLinks);

        return "dashboard";
    }

    //Profile

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);

        return "profile";
    }

    // Edit Profile

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);

        return "edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String phoneNumber,
                                @RequestParam String about,
                                @RequestParam("profilePic") MultipartFile profilePic,
                                Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
               .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setAbout(about);

        if (!profilePic.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(profilePic);
            user.setProfilePic(imageUrl);
        }

        userRepo.save(user);

        return "redirect:/profile";
    }

    // Settings
    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }

    @GetMapping("/settings/change-password")
    public String changePasswordPage() {
        return "change-password";
    }

    @PostMapping("/settings/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "redirect:/settings/change-password";
        }

        if(!newPassword.equals(confirmPassword)) {
            return "redirect:/settings/change-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepo.save(user);

        return "redirect:/settings";
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
    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@ModelAttribute UserForm userForm) {
        System.out.println("Processing registration");
        // fetch form data
        // UserForm
        System.out.println(userForm);
        // validate form data
        // save to database
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());

        User savedUser = userService.saveUser(user);
        System.out.println("User saved successfully");

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setToken(token);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        verificationTokenRepo.save(verificationToken);

        String verificationLink = "http://localhost:8081/verify-email?token=" + token;

        emailService.sendSimpleEmail(savedUser.getEmail(), "Verify Your ContactSphere Account", "Click the link below to verify your email:\n\n" + verificationLink);

        // Redirect to login page
        return "redirect:/register";
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
        userRepo.save(user);
        
        return "redirect:/login?verified=true";
    }
}
