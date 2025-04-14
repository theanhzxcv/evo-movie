package com.evo.identity.infrastructure.configuration;

import com.evo.constants.ErrConstants;
import com.evo.exception.AppException;
import com.evo.identity.application.constants.AdminConstants;
import com.evo.identity.application.constants.PermissionConstants;
import com.evo.identity.application.constants.RoleConstants;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.application.enums.EDefault;
import com.evo.identity.application.enums.EDefaultRole;
import com.evo.identity.domain.Permission;
import com.evo.identity.domain.Role;
import com.evo.identity.domain.User;
import com.evo.identity.domain.command.PermissionCmd;
import com.evo.identity.domain.command.RoleCmd;
import com.evo.identity.domain.command.RolePermissionCmd;
import com.evo.identity.domain.command.UserCmd;
import com.evo.identity.domain.command.UserDetailCmd;
import com.evo.identity.domain.command.UserRoleCmd;
import com.evo.identity.domain.repository.PermissionDomainRepository;
import com.evo.identity.domain.repository.RoleDomainRepository;
import com.evo.identity.domain.repository.UserDomainRepository;
import com.evo.identity.infrastructure.adapter.keycloak.KeycloakUtils;
import com.evo.identity.infrastructure.persistence.mapper.PermissionEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.RoleEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.PermissionEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.RoleEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserEntityRepository;
import com.evo.util.EvoIdUtils;
import com.evo.util.EvoModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataSeederConfig implements CommandLineRunner {

    private final KeycloakUtils keycloakUtils;
//    private final TfaService tfaService;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepository userEntityRepository;
    private final UserDomainRepository userDomainRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final RoleDomainRepository roleDomainRepository;
    private final RoleEntityMapperImpl roleEntityMapper;
    private final PermissionEntityRepository permissionEntityRepository;
    private final PermissionDomainRepository permissionDomainRepository;
    private final PermissionEntityMapperImpl permissionEntityMapper;

    @Override
    public void run(String... args) throws Exception {
        seedPermission();
        seedRoles();
        seedAdminAccount();
    }

    private void seedAdminAccount() {
        String adminEmail = AdminConstants.ADMIN_EMAIL;
        String adminName = AdminConstants.ADMIN_NAME;
        String adminPass = AdminConstants.ADMIN_PASSWORD;

        if (userEntityRepository.findByUserNameAndIsActive(adminName, EActive.ACTIVE.value).isEmpty()) {
            UserCmd userCmd = new UserCmd();
            userCmd.setUserName(adminName);
            UserDetailCmd userDetailCmd = new UserDetailCmd();
            userDetailCmd.setFirstName(adminName);
            userDetailCmd.setLastName(adminName);
            userDetailCmd.setEmailChange(adminEmail);
            userCmd.setUserDetailCmd(userDetailCmd);

            List<String> roleNames = List.of(RoleConstants.ADMIN_ROLE_NAME,
                    RoleConstants.USER_ROLE_NAME);
            List<Role> roles = roleEntityRepository
                    .findAllByNameIsInAndIsActive(roleNames, EActive.ACTIVE.value).stream()
                    .map(roleEntityMapper::toDomain)
                    .toList();
            List<UUID> roleIds = roles.stream().map(Role::getId).toList();

            List<UserRoleCmd> userRoleCmds = new ArrayList<>();
            for (UUID roleId : roleIds) {
                UserRoleCmd userRoleCmd = new UserRoleCmd();
                userRoleCmd.setId(EvoIdUtils.nextId());
                userRoleCmd.setUserId(userCmd.getId());
                userRoleCmd.setRoleId(roleId);
                userRoleCmds.add(userRoleCmd);
            }
            userCmd.setUserRoleCmds(userRoleCmds);
//        creationCmd.setSecretKey(tfaService.generateSecretKey());
//        keycloakUtils.registrationWithKeycloak(creationCmd);
            userCmd.setUserPass(passwordEncoder.encode(adminPass));
            User adminUser = new User(userCmd);

            userDomainRepository.save(adminUser);
        }
    }

    private void seedRoles() {
        if (roleEntityRepository.findByNameAndIsActive(RoleConstants.ADMIN_ROLE_NAME, EActive.ACTIVE.value).isEmpty()) {
            List<UUID> permissionIds = new ArrayList<>();
            Permission adminPermission = permissionEntityRepository
                    .findByNameAndIsActive(PermissionConstants.ADMIN_PERMISSION_NAME, EActive.ACTIVE.value)
                    .map(permissionEntityMapper::toDomain)
                    .orElseThrow(() -> new AppException(ErrConstants.PERMISSION_DETAIL_ERROR_001));
            permissionIds.add(adminPermission.getId());
            seedDefaultRoles(RoleConstants.ADMIN_ROLE_NAME,
                    RoleConstants.ADMIN_ROLE_DESCRIPTION,
                    RoleConstants.ADMIN_ROLE_ROOT,
                    RoleConstants.ADMIN_ROLE_TYPE,
                    permissionIds);
        }

        if (roleEntityRepository.findByNameAndIsActive(EDefaultRole.USER.value, EActive.ACTIVE.value).isEmpty()) {
            List<String> permissionNames = List.of(PermissionConstants.USER_PERMISSION_NAME_1,
                    PermissionConstants.USER_PERMISSION_NAME_2);
            List<Permission> userPermissions = permissionEntityRepository
                    .findAllByNameIsInAndIsActive(permissionNames, EActive.ACTIVE.value).stream()
                    .map(permissionEntityMapper::toDomain)
                    .toList();
            List<UUID> permissionIds = userPermissions.stream().map(Permission::getId).toList();
            seedDefaultRoles(RoleConstants.USER_ROLE_NAME,
                    RoleConstants.USER_ROLE_DESCRIPTION,
                    RoleConstants.USER_ROLE_ROOT,
                    RoleConstants.USER_ROLE_TYPE,
                    permissionIds);
        }
    }

    private void seedDefaultRoles(String roleName,
                                  String roleDescription,
                                  Long roleRoot,
                                  String roleType,
                                  List<UUID> permissionIds) {
        RoleCmd roleCmd = new RoleCmd();
        roleCmd.setId(EvoIdUtils.nextId());
        roleCmd.setName(roleName);
        roleCmd.setDescription(roleDescription);
        roleCmd.setRoot(roleRoot);
        roleCmd.setType(roleType);
        roleCmd.setIsDefault(EDefault.DEFAULT.value);

        List<RolePermissionCmd> rolePermissionCmds = new ArrayList<>();
        for (UUID permissionId : permissionIds) {
            RolePermissionCmd rolePermissionCmd = new RolePermissionCmd();
            rolePermissionCmd.setPermissionId(permissionId);
            rolePermissionCmds.add(rolePermissionCmd);
        }
        roleCmd.setRolePermissionCmds(rolePermissionCmds);
        Role role = new Role(roleCmd);
        roleDomainRepository.save(role);
    }

    private void seedPermission() {
        if (permissionEntityRepository.findByNameAndIsActive(PermissionConstants.ADMIN_PERMISSION_NAME, EActive.ACTIVE.value).isEmpty()
                || permissionEntityRepository.findByNameAndIsActive(PermissionConstants.USER_PERMISSION_NAME_1, EActive.ACTIVE.value).isEmpty()
                || permissionEntityRepository.findByNameAndIsActive(PermissionConstants.USER_PERMISSION_NAME_2, EActive.ACTIVE.value).isEmpty()) {
            seedDefaultPermissions(PermissionConstants.ADMIN_PERMISSION_NAME,
                    PermissionConstants.ADMIN_PERMISSION_DESCRIPTION,
                    PermissionConstants.ADMIN_RESOURCE,
                    PermissionConstants.ADMIN_SCOPE);
            seedDefaultPermissions(PermissionConstants.USER_PERMISSION_NAME_1,
                    PermissionConstants.USER_PERMISSION_DESC_1,
                    PermissionConstants.USER_RESOURCE_1,
                    PermissionConstants.USER_SCOPE_1);
            seedDefaultPermissions(PermissionConstants.USER_PERMISSION_NAME_2,
                    PermissionConstants.USER_PERMISSION_DESC_2,
                    PermissionConstants.USER_RESOURCE_2,
                    PermissionConstants.USER_SCOPE_2);
        }
    }

    private void seedDefaultPermissions(String permissionName,
                                        String permissionDescription,
                                        String permissionResource,
                                        String permissionScope) {
        PermissionCmd permissionCmd = new PermissionCmd();
        permissionCmd.setId(EvoIdUtils.nextId());
        permissionCmd.setName(permissionName);
        permissionCmd.setDescription(permissionDescription);
        permissionCmd.setResource(permissionResource);
        permissionCmd.setScope(permissionScope);
        Permission permission = new Permission(permissionCmd);

        permissionDomainRepository.save(permission);
    }
}