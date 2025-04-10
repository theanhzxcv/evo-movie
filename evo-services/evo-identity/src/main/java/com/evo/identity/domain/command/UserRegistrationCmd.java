package com.evo.identity.domain.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationCmd {
    private UUID id;
    private String userName;
    private String userPass;
    private String userEmail;
    private String firstName;
    private String lastName;
}
