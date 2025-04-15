package com.evo.identity.domain;

import com.evo.configuration.AuditableDomain;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.application.enums.EDefault;
import com.evo.identity.domain.command.RoleCmd;
import com.evo.identity.domain.command.RolePermissionCmd;
import com.evo.util.EvoIdUtils;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Role extends AuditableDomain {
    private UUID id;
    private String name;
    private String description;
    private Long isRoot;
    private Long isActive;
    private Long isDefault;
    private String type;
    private List<RolePermission> rolePermissions;

    public Role(RoleCmd cmd) {
        this.id = EvoIdUtils.nextId();
        this.name = cmd.getName();
        this.description = cmd.getDescription();
        this.isRoot = cmd.getIsRoot();
        this.isActive = EActive.ACTIVE.value;
        this.isDefault = cmd.getIsDefault();
        this.type = cmd.getType();
        this.updateRolePermission(cmd.getRolePermissionCmds());
    }

    public void update(RoleCmd cmd) {
        this.name = cmd.getName();
        this.description = cmd.getDescription();
        this.isRoot = cmd.getIsRoot();
        this.type = cmd.getType();
        this.updateRolePermission(cmd.getRolePermissionCmds());
    }

    public void updateRolePermission(List<RolePermissionCmd> cmds) {
        if (this.rolePermissions == null) {
            this.rolePermissions = new ArrayList<>();
        }

        if (!(this.rolePermissions instanceof ArrayList)) {
            this.rolePermissions = new ArrayList<>(this.rolePermissions);
        }

        this.rolePermissions.forEach(RolePermission::delete);

        for (RolePermissionCmd cmd : cmds) {
            RolePermission existingPermission = this.rolePermissions.stream()
                    .filter(rp -> rp.getPermissionId().equals(cmd.getPermissionId()))
                    .findFirst()
                    .orElseGet(() -> {
                        RolePermissionCmd newCmd = new RolePermissionCmd();
                        newCmd.setId(EvoIdUtils.nextId());
                        newCmd.setRoleId(this.id);
                        newCmd.setPermissionId(cmd.getPermissionId());
                        RolePermission rolePermission = new RolePermission(newCmd);
                        this.rolePermissions.add(rolePermission); // Now safe
                        return rolePermission;
                    });
            existingPermission.restore();
        }
    }

    public void delete() {
        this.isActive = EActive.INACTIVE.value;
        this.rolePermissions.forEach(RolePermission::delete);
    }

    public void restore() {
        this.isActive = EActive.ACTIVE.value;
        this.rolePermissions.forEach(RolePermission::restore);
    }

    public void enrichRolePermission(List<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }
}
