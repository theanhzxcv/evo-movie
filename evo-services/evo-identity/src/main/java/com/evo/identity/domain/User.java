package com.evo.identity.domain;

import com.evo.configuration.AuditableDomain;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.domain.command.TokenInfoCmd;
import com.evo.identity.domain.command.UserDetailCmd;
import com.evo.identity.domain.command.UserRegistrationCmd;
import com.evo.util.EvoIdUtils;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class User extends AuditableDomain {
    private UUID id;
    private String userName;
    private String userPass;
    private Long isActive;
    private String secretKey;
    private Long isTfaEnabled;
    private UserDetail userDetail;
    private TokenInfo tokenInfo;
    private UserActivity userActivities;
    private List<UserRole> userRoles;

    public User(UserRegistrationCmd cmd) {
        this.id = EvoIdUtils.nextId();
        this.userName = cmd.getUserName();
        this.userPass = cmd.getUserPass();
        this.isActive = EActive.ACTIVE.value;
    }

    public void saveUserDetail(UserDetailCmd cmd) {
        this.userDetail = new UserDetail(cmd);
    }

    public void saveTokenInfo(TokenInfoCmd cmd) {
        this.tokenInfo = new TokenInfo(cmd);
    }


}
