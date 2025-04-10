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

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
public class UserEntity extends AuditableEntity {
    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "USER_PASS")
    private String userPass;

    @Column(name = "IS_ACTIVE")
    private Long isActive;

    @Column(name = "SECRET_KEY")
    private String secretKey;

    @Column(name = "IS_TFA_ENABLED")
    private Long isTfaEnabled;
}
