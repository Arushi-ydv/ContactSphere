package com.contactsphere.services.impl;

import org.springframework.stereotype.Service;

import com.contactsphere.entities.SocialLink;
import com.contactsphere.repositories.SocialLinkRepo;
import com.contactsphere.services.SocialLinkService;

@Service
public class SocialLinkServiceImpl implements SocialLinkService{

    private final SocialLinkRepo socialLinkRepo;

    public SocialLinkServiceImpl(SocialLinkRepo socialLinkRepo) {
        this.socialLinkRepo = socialLinkRepo;
    }

    @Override
    public SocialLink saveSocialLink(SocialLink socialLink) {
        return socialLinkRepo.save(socialLink);
    } 

}
