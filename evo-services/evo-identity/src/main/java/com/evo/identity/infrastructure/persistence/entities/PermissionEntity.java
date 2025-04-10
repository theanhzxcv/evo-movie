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
@Table(name = "PERMISSION")
public class PermissionEntity extends AuditableEntity {
    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "RESOURCE")
    private String resource;

    @Column(name = "SCOPE")
    private String scope;

    @Column(name = "IS_ACTIVE")
    private Long isActive;
}
