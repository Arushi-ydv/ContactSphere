package com.contactsphere.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.contactsphere.entities.User;
import com.contactsphere.entities.VerificationToken;


@Repository
public interface VerificationTokenRepo extends JpaRepository<VerificationToken, Long>{
    
    Optional<VerificationToken> findByToken(String token);

    @Transactional
    void deleteByUser(User user);
}
