package com.contactsphere.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.contactsphere.entities.Contact;
import com.contactsphere.entities.User;
import com.contactsphere.exceptions.ResourceNotFoundException;
import com.contactsphere.repositories.UserRepo;
import com.contactsphere.services.ContactService;

import org.springframework.security.core.Authentication;

@Controller
public class DashboardController {

    private final UserRepo userRepo;
    private final ContactService contactService;

    public DashboardController(
            UserRepo userRepo,
            ContactService contactService) {

        this.userRepo = userRepo;
        this.contactService = contactService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        List<Contact> contacts =
                contactService.getAllContacts(user);

        long totalContacts = contacts.size();

        long favouriteContacts = contacts.stream()
                .filter(Contact::isFavourite)
                .count();

        long socialLinks = contacts.stream()
                .mapToLong(contact ->
                        contact.getLinks().size())
                .sum();

        model.addAttribute("user", user);
        model.addAttribute("contacts", contacts);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favouriteContacts", favouriteContacts);
        model.addAttribute("socialLinks", socialLinks);

        return "dashboard";
    }
}