package com.evo.identity.domain.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailCmd {
    private UUID id;
    private UUID userId;
    private UUID avatarId;
    private String firstName;
    private String lastName;
    private String emailVerified;
    private String email;
    private String linkVerify;
    private Instant linkExpireTime;
    private Long isVerified;
}
