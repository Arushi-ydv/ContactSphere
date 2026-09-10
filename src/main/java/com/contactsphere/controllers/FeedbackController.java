package com.contactsphere.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.contactsphere.entities.Feedback;
import com.contactsphere.forms.FeedbackForm;
import com.contactsphere.services.FeedbackService;

import jakarta.validation.Valid;

@Controller
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/feedback")
    public String feedbackPage(Model model){
        model.addAttribute("feedbackForm", new FeedbackForm());
        return "feedback";
    }

    @PostMapping("/feedback")
    public String saveFeedback(
        @Valid @ModelAttribute("feedbackForm") FeedbackForm feedbackForm,
        BindingResult bindingResult,
        Model model) {

        if(bindingResult.hasErrors()) {
            return "feedback";
        }

        Feedback feedback = new Feedback();
        feedback.setName(feedbackForm.getName());
        feedback.setEmail(feedbackForm.getEmail());
        feedback.setMessage(feedbackForm.getMessage());

        feedbackService.saveFeedback(feedback);

        return "redirect:/home";
    }
}
