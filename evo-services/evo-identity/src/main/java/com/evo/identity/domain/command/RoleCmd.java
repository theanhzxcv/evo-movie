package com.evo.identity.domain.command;

import com.evo.identity.domain.RolePermission;
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
public class RoleCmd {
    private UUID id;
    private String name;
    private String description;
    private Long isRoot;
    private Long isActive;
    private Long isDefault;
    private String type;
    private List<RolePermissionCmd> rolePermissionCmds;
}
