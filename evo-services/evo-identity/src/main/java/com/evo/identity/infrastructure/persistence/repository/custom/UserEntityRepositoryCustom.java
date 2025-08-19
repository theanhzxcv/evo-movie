package com.evo.identity.infrastructure.persistence.repository.custom;

import com.evo.identity.application.model.AssignPermissionResModel;
import com.evo.identity.application.model.AssignRoleResModel;
import com.evo.identity.domain.query.UserQuery;
import com.evo.identity.infrastructure.persistence.entities.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserEntityRepositoryCustom {

    List<UserEntity> searchUsers(UserQuery query);

    Long countUsers(UserQuery query);

    List<AssignRoleResModel> getAssignRolesByUserId(UUID roleId);

    List<UserEntity> searchUsersByRoleId(UUID roleId);

    Long countUsersByRoleId(UUID roleId);
}
