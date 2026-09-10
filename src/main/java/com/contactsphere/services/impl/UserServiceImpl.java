package com.contactsphere.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.contactsphere.entities.User;
import com.contactsphere.repositories.UserRepo;
import com.contactsphere.services.UserService;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepo userRepo,
            PasswordEncoder passwordEncoder) {

        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;        
    }

    @Override
    public User saveUser(User user) {
        if(user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(
                passwordEncoder.encode(user.getPassword())
            );
        }
        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id){
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email){
        return userRepo.findByEmail(email);
    }

    @Override
    public Optional<User> updateUser(User user){
        return Optional.of(userRepo.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public boolean isUserExist(Long userId) {
        return userRepo.existsById(userId);
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
}
