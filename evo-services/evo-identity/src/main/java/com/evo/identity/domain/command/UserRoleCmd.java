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
public class UserRoleCmd {
    private UUID id;
    private UUID userId;
    private UUID roleId;
}
