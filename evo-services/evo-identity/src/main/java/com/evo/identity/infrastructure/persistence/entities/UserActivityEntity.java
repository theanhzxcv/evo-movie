package com.evo.identity.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_ACTIVITY")
public class UserActivityEntity {
    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "RETRY_COUNT")
    private Long retryCount;

    @Column(name = "LOCK_UNTIL")
    private Instant lockUntil;

    @Column(name = "ACTIVITY")
    private String activity;

    @Column(name = "LOG_AT")
    private Instant logAt;
}
