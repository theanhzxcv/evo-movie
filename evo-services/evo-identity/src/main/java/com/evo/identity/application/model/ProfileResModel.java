package com.evo.identity.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResModel {
    private UUID id;
    private UUID userId;
    private UUID avatarId;
    private String bio;
    private String firstName;
    private String lastName;
    private String emailVerified;
    private String email;
    private String gender;
    private String phoneNumber;
    private Date dateOfBirth;
    private String addressLine;
    private String province;
    private String country;
    private Long isVerified;
}
