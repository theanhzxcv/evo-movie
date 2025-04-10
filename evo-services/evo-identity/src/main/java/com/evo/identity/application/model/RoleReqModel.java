package com.evo.identity.application.model;

import com.evo.identity.application.enums.EActive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleReqModel {
    @NotBlank(message = "INPUT_ERROR_001")
    @Size(min = 4, max = 255, message = "INPUT_ERROR_002")
    private String name;

    @Size(min = 4, max = 255, message = "INPUT_ERROR_002")
    private String description;

    @NotNull(message = "INPUT_ERROR_001")
    private Long root;

    @NotBlank(message = "INPUT_ERROR_001")
    private String type;

    @NotNull(message = "INPUT_ERROR_001")
    @Size(min = 1, message = "INPUT_ERROR_001")
    private List<UUID> permissionIds;
}
