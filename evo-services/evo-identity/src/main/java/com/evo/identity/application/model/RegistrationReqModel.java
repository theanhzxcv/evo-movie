package com.evo.identity.application.model;

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
public class RegistrationReqModel {
    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 1, max = 255, message = "INPUT_ERROR_002")
    private String userName;

    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 1, max = 255, message = "INPUT_ERROR_002")
    @Email(message = "EMAIL_ERROR_001")
    private String userEmail;

    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 8, max = 255, message = "PASSWORD_ERROR_001")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,255}$",
            message = "PASSWORD_ERROR_002"
    )
    private String userPass;

    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 1, max = 255, message = "INPUT_ERROR_002")
    private String firstName;

    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 1, max = 255, message = "INPUT_ERROR_002")
    private String lastName;
}
