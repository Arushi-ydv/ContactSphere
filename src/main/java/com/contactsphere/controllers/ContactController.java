package com.contactsphere.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactsphere.entities.Contact;
import com.contactsphere.entities.SocialLink;
import com.contactsphere.entities.User;
import com.contactsphere.repositories.UserRepo;
import com.contactsphere.services.ContactService;
import com.contactsphere.services.ExcelService;
import com.contactsphere.services.PdfService;
import com.contactsphere.services.SocialLinkService;

@Controller
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SocialLinkService socialLinkService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/contacts")
    public String contacts(@RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model, Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        int pageSize = 5;
        
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Contact> contactPage;

        if(keyword != null && !keyword.trim().isEmpty()) {
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

        Contact contact = new Contact();
        model.addAttribute("contact", contact);
        return "add-contact";
    }

    @GetMapping("/contacts/{id}")
    public String contactDetail(@PathVariable Long id, Model model) {

        Contact contact = contactService.getContactById(id);
        model.addAttribute("contact", contact);
        return "contact-detail";
    }

    @GetMapping("/contacts/{id}/edit")
    public String editContactForm(@PathVariable Long id, Model model) {

        Contact contact = contactService.getContactById(id);
        model.addAttribute("contact", contact);
        return "edit-contact";
    }

    @PostMapping("/contacts")
    public String saveContact(@ModelAttribute Contact contact, Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        contact.setUser(user);
        contactService.saveContact(contact);
        return "redirect:/contacts";
    }

    @PostMapping("/contacts/{id}/update")
    public String updateContact(@PathVariable Long id, @ModelAttribute Contact contact, Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("user not found"));

        Contact existingContact = contactService.getContactById(id);

        existingContact.setName(contact.getName());
        existingContact.setEmail(contact.getEmail());
        existingContact.setPhoneNumber(contact.getPhoneNumber());
        existingContact.setAddress(contact.getAddress());
        existingContact.setDescription(contact.getDescription());
        existingContact.setFavourite(contact.isFavourite());

        existingContact.setUser(user);

        contactService.saveContact(existingContact);

        return "redirect:/contacts/" + id;
    }

    @PostMapping("/contacts/{id}/delete")
    public String deleteContact(@PathVariable Long id, Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = contactService.getContactById(id);

        if(!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }
        contactService.deleteContact(id);
        return "redirect:/contacts";
    }

    @PostMapping("/contacts/{id}/favourite")
    public String togglefavourite(@PathVariable Long id, Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = contactService.getContactById(id);

        if(!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }
        contact.setFavourite(!contact.isFavourite());
        contactService.saveContact(contact);

        return "redirect:/contacts/" + id;
    }

    @PostMapping("/contacts/{contactId}/social-links")
    public String addSocialLink (@PathVariable Long contactId, @ModelAttribute SocialLink socialLink, Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = contactService.getContactById(contactId);

        if(!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }
        socialLink.setContact(contact);
        socialLinkService.saveSocialLink(socialLink);

        return "redirect:/contacts/" + contactId;
    }

    @PostMapping("/contacts/{contactId}/social-links/{socialLinkId}/delete")
    public String deleteSocialLink(@PathVariable Long contactId, @PathVariable Long socialLinkId, Authentication authentication) {
        
        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        Contact contact = contactService.getContactById(contactId);

        if(!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }
        contact.getLinks().removeIf(link -> link.getId().equals(socialLinkId));
        contactService.saveContact(contact);

        return "redirect:/contacts/" + contactId;
    }

    @GetMapping("/contacts/export/excel")
    public ResponseEntity<byte[]> exportContactsToExcel(Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Contact> contacts = contactService.getAllContacts(user);

        byte[] excelData = excelService.exportContactsToExcel(contacts);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                     "attachment; filename=contacts.xlsx"
                )
                .contentType(
                    MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    )
                )
                .body(excelData);
    }

    @GetMapping("contacts/export/pdf")
    public ResponseEntity<byte[]> exportContactsToPdf(Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName())
               .orElseThrow(() -> new RuntimeException("User not found"));

        List<Contact> contacts = contactService.getAllContacts(user);

        byte[] pdfData = pdfService.exportContactsToPdf(contacts);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=contacts.pdf"
                )
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
}
