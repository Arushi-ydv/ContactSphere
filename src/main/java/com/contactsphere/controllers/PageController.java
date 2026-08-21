package com.contactsphere.controllers;

import com.contactsphere.entities.User;
import com.contactsphere.forms.UserForm;
import com.contactsphere.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class PageController {

    @Autowired
    private UserService userService;

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

        userService.saveUser(user);
        System.out.println("User saved successfully");

        // Redirect to login page
        return "redirect:/register";
    }
}
