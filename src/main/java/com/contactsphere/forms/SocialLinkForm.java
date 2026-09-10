package com.contactsphere.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialLinkForm {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 500)
    @Pattern(
        regexp = "https?://.*",
        message = "Link must start with http:// or https://"
    )
    private String link;
}