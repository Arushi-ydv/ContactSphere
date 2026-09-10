package com.contactsphere.services;

import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.contactsphere.entities.Providers;
import com.contactsphere.entities.User;
import com.contactsphere.repositories.UserRepo;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    
    private final UserRepo userRepo;

    public CustomOAuth2UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String email;
        String name;
        String providerUserId;
        Providers provider;

        // GOOGLE
        if("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");

            providerUserId = (String) attributes.get("sub");

            provider = Providers.GOOGLE;

        } else if ("github".equals(registrationId))  {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");

            if(name == null || name.isBlank()) {
                name = (String) attributes.get("login");
            }
            providerUserId = String.valueOf(attributes.get("id"));

            provider = Providers.GITHUB;

        } else {
            throw new IllegalArgumentException(
                "Unsupported OAuth2 provider: " + registrationId);
        }

        if(email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                "Email not provided by OAuth2 provider"
            );
        }

       // Find existing user
        User user = userRepo.findByEmail(email).orElse(null);

        // If user does not exist, create one
        if(user == null){
            user = new User();

            user.setEmail(email);
            user.setName(name);
        }
                    
        // Update OAuth information
        user.setProvider(provider);
        user.setProviderUserId(providerUserId);
        user.setEnabled(true);
        user.setEmailVerified(true);

        userRepo.save(user);

        return new DefaultOAuth2User(
            java.util.List.of( new SimpleGrantedAuthority("ROLE_USER")
            ),
            attributes,
            "email"
        );
    }
}
