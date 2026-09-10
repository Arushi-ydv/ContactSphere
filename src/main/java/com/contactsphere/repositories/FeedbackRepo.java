package com.contactsphere.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contactsphere.entities.Feedback;

public interface FeedbackRepo extends JpaRepository<Feedback, Long> {

}
