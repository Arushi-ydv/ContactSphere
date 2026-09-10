package com.contactsphere.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(
            ResourceNotFoundException exception,
            Model model) {

        model.addAttribute("errorMessage",
                "The requested resource was not found.");

        return "error";
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(
            UnauthorizedException exception,
            Model model) {

        model.addAttribute("errorMessage",
                "You are not authorized to access this resource.");

        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(
            Exception exception,
            Model model) {

        model.addAttribute("errorMessage",
                "Something went wrong. Please try again.");

        return "error";
    }
}