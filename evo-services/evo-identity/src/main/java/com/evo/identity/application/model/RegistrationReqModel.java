package com.evo.identity.application.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RegistrationReqModel {
    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 1, max = 255, message = "INPUT_ERROR_002")
    private String userName;

    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 1, max = 255, message = "INPUT_ERROR_002")
    @Email(message = "EMAIL_ERROR_001")
    private String email;

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

    @Size(max = 500, message = "INPUT_ERROR_002")
    private String bio;

    @Pattern(regexp = "^(male|female|other)?$", message = "INPUT_ERROR_002")
    private String gender;

    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "PHONE_ERROR_002"
    )
    private String phoneNumber;

    @NotNull(message = "INPUT_ERROR_001")
    @Past(message = "INPUT_ERROR_002")
    private Date dateOfBirth;

    @Size(max = 100, message = "INPUT_ERROR_002")
    private String addressLine;

    @Size(max = 100, message = "INPUT_ERROR_002")
    private String province;

    @Size(max = 100, message = "INPUT_ERROR_002")
    private String country;
}
