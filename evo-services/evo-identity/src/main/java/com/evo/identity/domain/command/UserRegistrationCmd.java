package com.evo.identity.domain.command;

import com.evo.identity.domain.UserRole;
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
public class UserRegistrationCmd {
    private UUID id;
    private String userName;
    private String userPass;
    private Long isActive;
    private String secretKey;
    private Long isTfaEnabled;
    private UserDetailCmd userDetailCmd;
    private List<UserRoleCmd> userRoleCmds;
}
