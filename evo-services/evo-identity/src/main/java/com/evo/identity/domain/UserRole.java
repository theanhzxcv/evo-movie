package com.evo.identity.domain;

import com.evo.configuration.AuditableDomain;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.domain.command.UserRoleCmd;
import com.evo.util.EvoIdUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserRole extends AuditableDomain {
    private UUID id;
    private UUID userId;
    private UUID roleId;
    private Long isActive;

    public UserRole(UserRoleCmd cmd) {
        this.id = EvoIdUtils.nextId();
        this.userId = cmd.getUserId();
        this.roleId = cmd.getRoleId();
        this.isActive = EActive.ACTIVE.value;
    }

    public void delete() {
        this.isActive = EActive.INACTIVE.value;
    }

    public void restore() {
        this.isActive = EActive.ACTIVE.value;
    }
}
