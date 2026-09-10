package com.contactsphere.services;

import java.util.List;
import java.util.Optional;

import com.contactsphere.entities.User;

public interface UserService {

    User saveUser(User user);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    Optional<User> updateUser(User user);

    void deleteUser(Long id);

    boolean isUserExist(Long userId);

    boolean isUserExistByEmail(String email);

    List<User> getAllUsers();

}
