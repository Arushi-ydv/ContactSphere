
package com.contactsphere.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactsphere.entities.Contact;
import com.contactsphere.entities.SocialLink;
import com.contactsphere.entities.User;
import com.contactsphere.exceptions.ResourceNotFoundException;
import com.contactsphere.exceptions.UnauthorizedException;
import com.contactsphere.forms.ContactForm;
import com.contactsphere.forms.SocialLinkForm;
import com.contactsphere.repositories.UserRepo;
import com.contactsphere.services.ContactService;
import com.contactsphere.services.SocialLinkService;

import jakarta.validation.Valid;

@Controller
public class ContactController {

    private final ContactService contactService;
    private final UserRepo userRepo;
    private final SocialLinkService socialLinkService;

    public ContactController(
            ContactService contactService,
            UserRepo userRepo,
            SocialLinkService socialLinkService) {

        this.contactService = contactService;
        this.userRepo = userRepo;
        this.socialLinkService = socialLinkService;
    }

    @GetMapping("/contacts")
    public String contacts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int pageSize = 5;

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Contact> contactPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            contactPage = contactService.searchContacts(user, keyword, pageable);
        } else {
            contactPage = contactService.getContactsByUser(user, pageable);
        }

        model.addAttribute("contacts", contactPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contactPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "contacts";
    }

    @GetMapping("/contacts/add")
    public String addContactForm(Model model) {

        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        return "add-contact";
    }

    @GetMapping("/contacts/{id}")
    public String contactDetail(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact contact = contactService.getContactById(id);

        if (!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("Unauthorized");
        }

        model.addAttribute("contact", contact);
        model.addAttribute("socialLinkForm", new SocialLinkForm());
        return "contact-detail";
    }

    @GetMapping("/contacts/{id}/edit")
    public String editContactForm(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact contact = contactService.getContactById(id);

        if (!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("Unauthorized");
        }

        ContactForm contactForm = new ContactForm();

        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());

        model.addAttribute("contact", contact);
        model.addAttribute("contactForm", contactForm);
        return "edit-contact";
    }

    @PostMapping("/contacts")
    public String saveContact(
            @Valid @ModelAttribute("contactForm") ContactForm contactForm,
            BindingResult bindingResult,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            return "add-contact";
        }

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact contact = new Contact();

        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());

        contact.setUser(user);
        contactService.saveContact(contact);

        return "redirect:/contacts";
    }

    @PostMapping("/contacts/{id}/update")
    public String updateContact(
            @PathVariable Long id,
            @Valid @ModelAttribute("contactForm") ContactForm contactForm,
            BindingResult bindingResult,
            Model model,
            Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact existingContact = contactService.getContactById(id);

        if (!existingContact.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("Unauthorized");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("contact", existingContact);
            return "edit-contact";
        }

        existingContact.setName(contactForm.getName());
        existingContact.setEmail(contactForm.getEmail());
        existingContact.setPhoneNumber(contactForm.getPhoneNumber());
        existingContact.setAddress(contactForm.getAddress());
        existingContact.setDescription(contactForm.getDescription());
        existingContact.setWebsiteLink(contactForm.getWebsiteLink());
        existingContact.setLinkedInLink(contactForm.getLinkedInLink());

        contactService.saveContact(existingContact);

        return "redirect:/contacts/" + id;
    }

    @PostMapping("/contacts/{id}/delete")
    public String deleteContact(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact contact = contactService.getContactById(id);

        if (!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("Unauthorized");
        }

        contactService.deleteContact(id);
        return "redirect:/contacts";
    }

    @PostMapping("/contacts/{id}/favourite")
    public String togglefavourite(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact contact = contactService.getContactById(id);

        if (!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("Unauthorized");
        }

        contact.setFavourite(!contact.isFavourite());
        contactService.saveContact(contact);

        return "redirect:/contacts/" + id;
    }

    @PostMapping("/contacts/{contactId}/social-links")
    public String addSocialLink(
            @PathVariable Long contactId,
            @Valid @ModelAttribute("socialLinkForm") SocialLinkForm socialLinkForm,
            BindingResult bindingResult,
            Model model,
            Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact contact = contactService.getContactById(contactId);

        if (!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("Unauthorized");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("contact", contact);
            return "contact-detail";
        }

        SocialLink socialLink = new SocialLink();
        socialLink.setTitle(socialLinkForm.getTitle());
        socialLink.setLink(socialLinkForm.getLink());
        socialLink.setContact(contact);

        socialLinkService.saveSocialLink(socialLink);

        return "redirect:/contacts/" + contactId;
    }

    @PostMapping("/contacts/{contactId}/social-links/{socialLinkId}/delete")
    public String deleteSocialLink(
            @PathVariable Long contactId,
            @PathVariable Long socialLinkId,
            Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact contact = contactService.getContactById(contactId);

        if (!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("Unauthorized");
        }

        contact.getLinks().removeIf(link -> link.getId().equals(socialLinkId));
        contactService.saveContact(contact);

        return "redirect:/contacts/" + contactId;
    }
}
