package com.evo.identity.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResModel {
    private UUID id;
    private UUID userId;
    private String emailVerified;
    private String email;
    private String firstName;
    private String lastName;
    private Long isVerified;
}
