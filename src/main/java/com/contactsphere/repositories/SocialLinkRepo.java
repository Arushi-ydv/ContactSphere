package com.contactsphere.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contactsphere.entities.SocialLink;

public interface SocialLinkRepo extends JpaRepository<SocialLink, Long> {
      
}
