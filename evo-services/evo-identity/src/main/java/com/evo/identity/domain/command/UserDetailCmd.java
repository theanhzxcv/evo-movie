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
    private String bio;
    private String firstName;
    private String lastName;
    private String gender;
    private String phoneNumber;
    private Date dateOfBirth;
    private String addressLine;
    private String province;
    private String country;
    private String emailVerified;
    private String email;
    private String linkVerify;
    private Date expireAt;
    private Long isVerified;
    private Date verifiedAt;
}
