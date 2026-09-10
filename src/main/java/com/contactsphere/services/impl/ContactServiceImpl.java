package com.contactsphere.services.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.contactsphere.entities.Contact;
import com.contactsphere.entities.User;
import com.contactsphere.exceptions.ResourceNotFoundException;
import com.contactsphere.repositories.ContactRepo;
import com.contactsphere.services.ContactService;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepo contactRepo;

    public ContactServiceImpl(ContactRepo contactRepo) {
        this.contactRepo = contactRepo;
    }

    @Override
    public Contact saveContact(Contact contact) {
        return contactRepo.save(contact);
    }

    @Override
    public List<Contact> getAllContacts(User user) {
        return contactRepo.findByUser(user);
    }

    @Override
    public List<Contact> searchContacts(User user, String name) {
        return contactRepo.findByUserAndNameContainingIgnoreCase(user, name);
    }

    @Override
    public Page<Contact> searchContacts(
        User user, String name, Pageable pageable) {
            return contactRepo.findByUserAndNameContainingIgnoreCase(user, name, pageable);
    }

    @Override
    public Page<Contact> getContactsByUser(User user, Pageable pageable) {
        return contactRepo.findByUser(user, pageable);
    }

    @Override
    public Contact getContactById(Long id) {
        return contactRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
    }

    @Override
    public void deleteContact(Long id) {
        contactRepo.deleteById(id);
    }
}
