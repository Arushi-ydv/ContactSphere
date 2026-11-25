
package com.contactsphere.forms;

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
public class ProfileForm {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Pattern(
        regexp = "^$|^[0-9+()\\-\\s]{7,20}$",
        message = "Please enter a valid phone number"
    )
    private String phoneNumber;

    @Size(max = 1000, message = "About cannot exceed 1000 characters")
    private String about;
}
