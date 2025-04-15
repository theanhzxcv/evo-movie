package com.evo.identity.domain;

import com.evo.configuration.AuditableDomain;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.domain.command.RolePermissionCmd;
import com.evo.identity.domain.command.TokenInfoCmd;
import com.evo.identity.domain.command.UserCmd;
import com.evo.identity.domain.command.UserDetailCmd;
import com.evo.identity.domain.command.UserRegistrationCmd;
import com.evo.identity.domain.command.UserRoleCmd;
import com.evo.util.EvoIdUtils;
import lombok.*;

import java.util.ArrayList;
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
        this.updateUserDetail(cmd.getUserDetailCmd());
        this.updateUserRole(cmd.getUserRoleCmds());
    }

    public User(UserCmd cmd) {
        this.id = EvoIdUtils.nextId();
        this.userName = cmd.getUserName();
        this.userPass = cmd.getUserPass();
        this.isActive = EActive.ACTIVE.value;
        this.updateUserDetail(cmd.getUserDetailCmd());
        this.updateUserRole(cmd.getUserRoleCmds());
    }

    public void update(UserCmd cmd) {
        this.userName = cmd.getUserName();
        this.updateUserDetail(cmd.getUserDetailCmd());
        this.updateUserRole(cmd.getUserRoleCmds());
    }

    public void updateUserRole(List<UserRoleCmd> cmds) {
        if (this.userRoles == null) {
            this.userRoles = new ArrayList<>();
        }

        if (!(this.userRoles instanceof ArrayList)) {
            this.userRoles = new ArrayList<>(this.userRoles);
        }

        this.userRoles.forEach(UserRole::delete);

        for (UserRoleCmd cmd : cmds) {
            UserRole existingRole = this.userRoles.stream()
                    .filter(ur -> ur.getRoleId().equals(cmd.getRoleId()))
                    .findFirst()
                    .orElseGet(() -> {
                        UserRoleCmd newCmd = new UserRoleCmd();
                        newCmd.setId(EvoIdUtils.nextId());
                        newCmd.setUserId(this.id);
                        newCmd.setRoleId(cmd.getRoleId());
                        UserRole userRole = new UserRole(newCmd);
                        this.userRoles.add(userRole); // Now safe
                        return userRole;
                    });
            existingRole.restore();
        }
    }

    public void updateUserDetail(UserDetailCmd cmd) {
        if (this.userDetail == null) {
            cmd.setUserId(this.id);
            this.userDetail = new UserDetail(cmd);
        } else {
            this.userDetail.update(cmd);
        }
    }

    public void saveTokenInfo(TokenInfoCmd cmd) {
        this.tokenInfo = new TokenInfo(cmd);
    }

    public void delete() {
        this.isActive = EActive.INACTIVE.value;
        this.userRoles.forEach(UserRole::delete);
    }

    public void restore() {
        this.isActive = EActive.ACTIVE.value;
        this.userRoles.forEach(UserRole::restore);
    }

    public void enrichUserRole(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
}
