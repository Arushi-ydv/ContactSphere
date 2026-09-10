package com.contactsphere.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class ContactForm {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Email(message = "Please enter a valid email")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Pattern(
        regexp = "^$|^[0-9+()\\-\\s]{7,20}$",
        message = "Please enter a valid phone number"
    )     
    private String phoneNumber;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 500, message = "Website link cannot exceed 500 characters")
    private String websiteLink;

    @Size(max = 500, message = "LinkedIn link cannot exceed 500 characters")
    private String linkedInLink;
}
