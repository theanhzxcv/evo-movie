package com.evo.identity.domain;

import com.evo.configuration.AuditableDomain;
import com.evo.identity.domain.command.UserDetailCmd;
import com.evo.util.EvoIdUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail extends AuditableDomain {
    private UUID id;
    private UUID userId;
    private UUID avatarId;
    private String firstName;
    private String lastName;
    private String email;
    private String emailChange;
    private String linkVerify;
    private Instant linkExpireTime;
    private Long isVerified;

    public UserDetail(UserDetailCmd cmd) {
        this.id = EvoIdUtils.nextId();
        this.userId = cmd.getUserId();
        this.emailChange = cmd.getEmailChange();
    }

    private void update() {

    }

    private void verify() {

    }
}
