package com.evo.identity.domain;

import com.evo.configuration.AuditableDomain;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.domain.command.PermissionCmd;
import com.evo.identity.infrastructure.persistence.entities.PermissionEntity;
import com.evo.util.EvoIdUtils;
import lombok.*;

import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends AuditableDomain {
    private UUID id;
    private String name;
    private String description;
    private String resource;
    private String scope;
    private Long isActive;

    public Permission(PermissionCmd cmd) {
        this.id = EvoIdUtils.nextId();
        this.name = cmd.getName();
        this.description = cmd.getDescription();
        this.resource = cmd.getResource();
        this.scope = cmd.getScope();
        this.isActive = EActive.ACTIVE.value;
    }

    public void update(PermissionCmd cmd) {
        this.name = cmd.getName();
        this.description = cmd.getDescription();
        this.resource = cmd.getResource();
        this.scope = cmd.getScope();
    }

    public void delete() {
        this.isActive = EActive.INACTIVE.value;
    }

    public void restore() {
        this.isActive = EActive.ACTIVE.value;
    }
}
