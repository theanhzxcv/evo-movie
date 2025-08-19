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
public class UserDetailResModel {
    private UUID id;
    private String userName;
    private String userPass;
    private String secretKey;
    private String isTfaEnabled;
    private Long isActive;
    private ProfileResModel userDetail;
    private List<AssignRoleResModel> assignRoles;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
}
