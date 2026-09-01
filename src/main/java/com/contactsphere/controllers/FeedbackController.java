package com.contactsphere.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.contactsphere.entities.Feedback;
import com.contactsphere.services.FeedbackService;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/feedback")
    public String feedbackPage(){
        return "feedback";
    }

    @PostMapping("/feedback")
    public String saveFeedback(@ModelAttribute Feedback feedback) {

        feedbackService.saveFeedback(feedback);

        return "redirect:/home";
    }
}
