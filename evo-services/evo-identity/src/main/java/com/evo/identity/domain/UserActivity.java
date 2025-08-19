package com.evo.identity.domain;

import com.evo.configuration.AuditableDomain;
import com.evo.identity.domain.command.UserActivityCmd;
import com.evo.util.EvoIdUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserActivity extends AuditableDomain {
    private UUID id;
    private String userName;
    private Long retryCount;
    private Date lockUntil;
    private Long status;
    private String activity;

    public UserActivity(UserActivityCmd cmd) {
        this.id = EvoIdUtils.nextId();
        this.userName = cmd.getUserName();
        this.retryCount = cmd.getRetryCount();
        this.lockUntil = cmd.getLockUntil();
        this.status = cmd.getStatus();
        this.activity = cmd.getActivity();
    }

    public void update(UserActivityCmd cmd) {
        this.retryCount = cmd.getRetryCount();
        this.lockUntil = cmd.getLockUntil();
    }
}
