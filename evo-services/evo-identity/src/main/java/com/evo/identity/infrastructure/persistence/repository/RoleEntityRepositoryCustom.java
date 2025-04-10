package com.evo.identity.infrastructure.persistence.repository;

import com.evo.identity.application.model.AssignPermissionResModel;
import com.evo.identity.domain.query.RoleQuery;
import com.evo.identity.infrastructure.persistence.entities.RoleEntity;

import java.util.List;
import java.util.UUID;

public interface RoleEntityRepositoryCustom {
    List<RoleEntity> searchRoles(RoleQuery query);

    Long countRoles(RoleQuery query);

    List<AssignPermissionResModel> getAssignPermissionsByRoleId(UUID roleId);
}
