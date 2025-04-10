package com.evo.identity.application.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResModel {
    private UUID id;
    private String name;
    private String description;
    private String resource;
    private String scope;
    private Long isActive;
}
