package com.evo.identity.application.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionReqModel {
    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 4, max = 255, message = "INPUT_ERROR_002")
    private String name;

    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 4, max = 255, message = "INPUT_ERROR_002")
    private String description;

    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 4, max = 255, message = "INPUT_ERROR_002")
    private String resource;

    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 4, max = 255, message = "INPUT_ERROR_002")
    private String scope;
}
