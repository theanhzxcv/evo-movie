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
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_DETAIL")
public class UserDetailEntity extends AuditableEntity {
    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "USER_ID")
    private UUID userId;

    @Column(name = "AVATAR_ID")
    private UUID avatarId;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "EMAIL_CHANGE")
    private String emailChange;

    @Column(name = "LINK_VERIFY")
    private String linkVerify;

    @Column(name = "LINK_EXPIRE_TIME")
    private Instant linkExpireTime;

    @Column(name = "IS_VERIFIED")
    private Long isVerified;
}
