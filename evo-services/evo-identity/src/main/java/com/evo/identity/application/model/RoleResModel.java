package com.evo.identity.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResModel {
    private UUID id;
    private String name;
    private String description;
    private Long root;
    private String type;
    private Long isActive;
    private List<UUID> permissionIds;
}
