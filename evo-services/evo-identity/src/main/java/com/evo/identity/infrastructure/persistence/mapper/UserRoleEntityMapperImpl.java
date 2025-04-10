package com.evo.identity.infrastructure.persistence.mapper;

import com.evo.identity.domain.UserRole;
import com.evo.identity.infrastructure.persistence.entities.UserRoleEntity;
import com.evo.support.DomainEntityMapper;
import com.evo.util.EvoModelMapperUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRoleEntityMapperImpl implements DomainEntityMapper<UserRole, UserRoleEntity> {

    @Override
    public UserRole toDomain(UserRoleEntity entity) {
        return EvoModelMapperUtils.toObject(entity, UserRole.class);
    }

    @Override
    public List<UserRole> toDomainList(List<UserRoleEntity> entities) {
        return List.of();
    }

    @Override
    public UserRoleEntity toEntity(UserRole domain) {
        return EvoModelMapperUtils.toObject(domain, UserRoleEntity.class);
    }

    @Override
    public List<UserRoleEntity> toEntityList(List<UserRole> domains) {
        return List.of();
    }
}
