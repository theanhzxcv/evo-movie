package com.evo.identity.infrastructure.persistence.mapper;

import com.evo.identity.domain.Role;
import com.evo.identity.infrastructure.persistence.entities.RoleEntity;
import com.evo.support.DomainEntityMapper;
import com.evo.util.EvoModelMapperUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleEntityMapperImpl implements DomainEntityMapper<Role, RoleEntity> {

    @Override
    public Role toDomain(RoleEntity entity) {
        return EvoModelMapperUtils.toObject(entity, Role.class);
    }

    @Override
    public List<Role> toDomainList(List<RoleEntity> entities) {
        return List.of();
    }

    @Override
    public RoleEntity toEntity(Role domain) {
        return EvoModelMapperUtils.toObject(domain, RoleEntity.class);
    }

    @Override
    public List<RoleEntity> toEntityList(List<Role> domains) {
        return List.of();
    }
}
