package com.evo.identity.application.service.impl;

import com.evo.UserAuthority;
import com.evo.constants.ErrConstants;
import com.evo.exception.AppException;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.application.enums.ERootRole;
import com.evo.identity.domain.Permission;
import com.evo.identity.domain.Role;
import com.evo.identity.domain.RolePermission;
import com.evo.identity.domain.User;
import com.evo.identity.domain.UserRole;
import com.evo.identity.infrastructure.persistence.entities.RoleEntity;
import com.evo.identity.infrastructure.persistence.entities.UserEntity;
import com.evo.identity.infrastructure.persistence.mapper.PermissionEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.RoleEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.RolePermissionEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserRoleEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.PermissionEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.RoleEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.RolePermissionEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserRoleEntityRepository;
import com.evo.security.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorityService {

    private final UserEntityMapperImpl userEntityMapper;
    private final UserEntityRepository userEntityRepository;
    private final RoleEntityMapperImpl roleEntityMapper;
    private final RoleEntityRepository roleEntityRepository;
    private final UserRoleEntityMapperImpl userRoleEntityMapper;
    private final UserRoleEntityRepository userRoleEntityRepository;
    private final PermissionEntityMapperImpl permissionEntityMapper;
    private final PermissionEntityRepository permissionEntityRepository;
    private final RolePermissionEntityMapperImpl rolePermissionEntityMapper;
    private final RolePermissionEntityRepository rolePermissionEntityRepository;

    @Override
    public UserAuthority getUserAuthority(UUID userId) {
        return null;
    }

    @Override
    public UserAuthority getUserAuthority(String username) {
        User user = findUser(username);

        return buildUserAuthority(user);
    }

    @Override
    public UserAuthority getClientAuthority(UUID clientId) {
        return null;
    }

    private UserAuthority buildUserAuthority(User user) {
        List<UserRole> userRoles = userRoleEntityRepository
                .findAllByUserIdAndIsActive(user.getId(), EActive.ACTIVE.value)
                .stream()
                .map(userRoleEntityMapper::toDomain)
                .toList();

        List<UUID> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .toList();

        List<Role> roles = roleEntityRepository
                .findAllByIdIsInAndIsActive(roleIds, EActive.ACTIVE.value).stream()
                .map(roleEntityMapper::toDomain)
                .toList();

        List<RolePermission> rolePermissions = rolePermissionEntityRepository
                .findAllByRoleIdIsInAndIsActive(roleIds, EActive.ACTIVE.value)
                .stream()
                .map(rolePermissionEntityMapper::toDomain)
                .toList();

        List<UUID> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .toList();

        List<Permission> permissions = permissionEntityRepository
                .findAllByIdIsInAndIsActive(permissionIds, EActive.ACTIVE.value)
                .stream()
                .map(permissionEntityMapper::toDomain)
                .toList();

        List<String> grantedPermissions = rolePermissions.stream()
                .map(rolePermission -> {
                    UUID permissionId = rolePermission.getPermissionId();
                    Permission permission = permissions.stream()
                            .filter(p -> p.getId().equals(permissionId))
                            .findFirst()
                            .orElseThrow(() -> new AppException(ErrConstants.PERMISSION_DETAIL_ERROR_001));

                    return permission.getResource() + ":" + permission.getScope();
                })
                .toList();

        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setUserId(user.getId());
        userAuthority.setGrantedPermissions(grantedPermissions);

        boolean isRoot = roles.stream()
                .anyMatch(role -> Objects.equals(ERootRole.ROOT.value, role.getRoot()));

        userAuthority.setIsRoot(isRoot ? ERootRole.ROOT.value : ERootRole.NON_ROOT.value);

        return userAuthority;
    }

    private User findUser(String username) {
        UserEntity userEntity = userEntityRepository.findByUserNameAndIsActive(username, EActive.ACTIVE.value)
                .orElseThrow(() -> new AppException(ErrConstants.USER_DETAIL_ERROR_001));

        return userEntityMapper.toDomain(userEntity);
    }
}
