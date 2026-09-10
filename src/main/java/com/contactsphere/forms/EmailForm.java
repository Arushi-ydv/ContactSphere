package com.contactsphere.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailForm {

    @NotBlank
    @Email(message = "Please enter a valid email address")
    private String to;

    @NotBlank
    @Size(max = 200, message = "Subject must not exceed 200 characters")
    private String subject;

    @NotBlank
    @Size(max = 5000, message = "Message must not exceed 5000 characters")
    private String message;
}