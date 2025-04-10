package com.evo.identity.infrastructure.persistence.mapper;

import com.evo.identity.domain.Permission;
import com.evo.identity.domain.RolePermission;
import com.evo.identity.domain.command.RolePermissionCmd;
import com.evo.identity.infrastructure.persistence.entities.RolePermissionEntity;
import com.evo.support.DomainEntityMapper;
import com.evo.util.EvoModelMapperUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RolePermissionEntityMapperImpl implements DomainEntityMapper<RolePermission, RolePermissionEntity> {
    @Override
    public RolePermission toDomain(RolePermissionEntity entity) {
        return EvoModelMapperUtils.toObject(entity, RolePermission.class);
    }

    @Override
    public List<RolePermission> toDomainList(List<RolePermissionEntity> entities) {
        return List.of();
    }

    @Override
    public RolePermissionEntity toEntity(RolePermission domain) {
        return EvoModelMapperUtils.toObject(domain, RolePermissionEntity.class);
    }

    @Override
    public List<RolePermissionEntity> toEntityList(List<RolePermission> domains) {
        return List.of();
    }
}
