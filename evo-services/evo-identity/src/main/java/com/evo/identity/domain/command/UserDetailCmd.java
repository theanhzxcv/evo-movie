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
    private String emailChange;
    private String fullName;
    private String phoneNumber;
    private Date dateOfBirth;
}
