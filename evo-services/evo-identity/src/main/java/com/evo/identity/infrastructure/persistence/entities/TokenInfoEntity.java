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
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TOKEN_INFO")
public class TokenInfoEntity extends AuditableEntity {
    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "USER_ID")
    private UUID userId;

    @Column(name = "ACCESS_TOKEN")
    private String accessToken;

    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    @Column(name = "ACCESS_TOKEN_EXPIRE_AT")
    private Long accessTokenExpireAt;

    @Column(name = "REFRESH_TOKEN_EXPIRE_AT")
    private Long refreshTokenExpireAt;

    @Column(name = "TYPE")
    private String type;
}
