package com.evo.identity.infrastructure.persistence.repository;

import com.evo.identity.infrastructure.persistence.entities.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionEntityRepository extends JpaRepository<RolePermissionEntity, UUID> {

    List<RolePermissionEntity> findAllByRoleIdAndIsActive(UUID roleId, Long isActive);

    List<RolePermissionEntity> findAllByRoleIdIsInAndIsActive(List<UUID> roleIds, Long isActive);

}
