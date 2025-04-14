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
public class UserResModel {
    private UUID id;
    private String userName;
    private Long isActive;
    private UserDetailResModel userDetail;
    private List<UUID> roleIds;
}
