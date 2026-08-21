package com.contactsphere.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.contactsphere.entities.Contact;
import com.contactsphere.entities.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Long> {

    List<Contact> findByUser(User user);

    List<Contact> findByUserAndNameContainingIgnoreCase(User User, String name);
}
