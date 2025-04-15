package com.evo.identity.domain.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionCmd {
    private UUID id;
    private UUID roleId;
    private UUID permissionId;
    private Long isActive;
}
