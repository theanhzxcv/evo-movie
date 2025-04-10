package com.evo.identity.infrastructure.persistence.mapper;

import com.evo.identity.domain.Permission;
import com.evo.identity.infrastructure.persistence.entities.PermissionEntity;
import com.evo.support.DomainEntityMapper;
import com.evo.util.EvoModelMapperUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionEntityMapperImpl implements DomainEntityMapper<Permission, PermissionEntity> {

    @Override
    public Permission toDomain(PermissionEntity entity) {
        return EvoModelMapperUtils.toObject(entity, Permission.class);
    }

    @Override
    public List<Permission> toDomainList(List<PermissionEntity> entities) {
        return List.of();
    }

    @Override
    public PermissionEntity toEntity(Permission domain) {
        return EvoModelMapperUtils.toObject(domain, PermissionEntity.class);
    }

    @Override
    public List<PermissionEntity> toEntityList(List<Permission> domains) {
        return List.of();
    }
}
