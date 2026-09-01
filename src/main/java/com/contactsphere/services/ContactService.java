package com.contactsphere.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.contactsphere.entities.Contact;
import com.contactsphere.entities.User;

public interface ContactService {

    Contact saveContact(Contact contact);
    
    List<Contact> getAllContacts(User user);

    List<Contact> searchContacts(User user, String name);

    Page<Contact> getContactsByUser(User user, Pageable pageable);

    Page<Contact> searchContacts(User user, String name, Pageable pageable);

    Contact getContactById(Long id);

    void deleteContact(Long id);
}
