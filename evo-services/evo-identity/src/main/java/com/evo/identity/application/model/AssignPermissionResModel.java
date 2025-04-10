package com.evo.identity.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignPermissionResModel {
    private UUID id;
    private String name;
    private String description;
    private String resource;
    private String scope;
    private Long assigned;
}
