package com.evo.identity.infrastructure.persistence.repository;

import com.evo.identity.domain.query.PermissionQuery;
import com.evo.identity.infrastructure.persistence.entities.PermissionEntity;

import java.util.List;

public interface PermissionEntityRepositoryCustom {
    List<PermissionEntity> searchPermissions(PermissionQuery query);

    Long countPermissions(PermissionQuery query);
}
