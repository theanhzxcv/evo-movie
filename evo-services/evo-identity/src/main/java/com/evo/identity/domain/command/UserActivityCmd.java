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
public class UserActivityCmd {
    private UUID id;
    private String userName;
    private Long retryCount;
    private Date lockUntil;
    private Long status;
    private String activity;
}
