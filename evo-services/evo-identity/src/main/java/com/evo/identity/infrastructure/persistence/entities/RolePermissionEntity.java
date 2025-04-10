package com.evo.identity.infrastructure.persistence.entities;

import com.evo.configuration.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ROLE_PERMISSION")
public class RolePermissionEntity extends AuditableEntity {
    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "ROLE_ID")
    private UUID roleId;

    @Column(name = "PERMISSION_ID")
    private UUID permissionId;

    @Column(name = "IS_ACTIVE")
    private Long isActive;
}
