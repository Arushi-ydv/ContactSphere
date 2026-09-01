package com.contactsphere.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contactsphere.entities.Feedback;
import com.contactsphere.repositories.FeedbackRepo;
import com.contactsphere.services.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService{

    @Autowired
    private FeedbackRepo feedbackRepo;

    @Override
    public Feedback saveFeedback(Feedback feedback) {
       
        return feedbackRepo.save(feedback);
    }

    
}
