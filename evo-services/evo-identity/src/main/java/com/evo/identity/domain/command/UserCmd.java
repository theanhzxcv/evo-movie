package com.evo.identity.domain.command;

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
public class UserCmd {
    private UUID id;
    private String userName;
    private String userPass;
    private UserDetailCmd userDetailCmd;
    private List<UserRoleCmd> userRoleCmds;
}
