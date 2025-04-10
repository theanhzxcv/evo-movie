package com.evo.identity.application.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationReqModel {
    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 4, max = 255, message = "INPUT_ERROR_002")
    private String userName;

    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 8, max = 255, message = "PASSWORD_ERROR_001")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,255}$",
            message = "PASSWORD_ERROR_002"
    )
    private String userPass;
}
