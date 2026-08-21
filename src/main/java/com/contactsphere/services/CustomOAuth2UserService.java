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
        if(registrationId.equals("google")) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            providerUserId = (String) attributes.get("sub");

            provider = Providers.GOOGLE;
        } else if (registrationId.equals("github"))  {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");

            if(name == null) {
                name = (String) attributes.get("login");
            }
            providerUserId = String.valueOf(attributes.get("id"));
            provider = Providers.GITHUB;
        } else {
            throw new IllegalArgumentException(
                "Unsupported OAuth2 provider: " + registrationId);
        }

       // Find existing user
        User user = userRepo.findByEmail(email).orElse(null);

        // If user does not exist, create one
        if(user == null){
            User newUser = new User();

            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setProvider(provider);
            newUser.setProviderUserId(providerUserId);
            newUser.setEnabled(true);
            newUser.setEmailVerified(true);
        }
                    
        // Update provider information
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
