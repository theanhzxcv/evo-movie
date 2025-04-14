package com.evo.identity.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReqModel {
    private String userName;
    private String userPass;
    private String userEmail;
    private String firstName;
    private String lastName;
    private List<UUID> roleIds;
}
