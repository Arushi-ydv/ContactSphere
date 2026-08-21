package com.contactsphere.services;

import java.util.List;

import com.contactsphere.entities.Contact;
import com.contactsphere.entities.User;

public interface ContactService {

    Contact saveContact(Contact contact);
    
    List<Contact> getAllContacts(User user);

    List<Contact> searchContacts(User user, String name);

    Contact getContactById(Long id);

    void deleteContact(Long id);
}
