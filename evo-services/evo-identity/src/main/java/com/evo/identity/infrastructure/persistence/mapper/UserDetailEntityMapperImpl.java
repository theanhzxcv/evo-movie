package com.evo.identity.infrastructure.persistence.mapper;

import com.evo.identity.domain.UserDetail;
import com.evo.identity.infrastructure.persistence.entities.UserDetailEntity;
import com.evo.support.DomainEntityMapper;
import com.evo.util.EvoModelMapperUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDetailEntityMapperImpl implements DomainEntityMapper<UserDetail, UserDetailEntity> {
    @Override
    public UserDetail toDomain(UserDetailEntity entity) {
        return EvoModelMapperUtils.toObject(entity, UserDetail.class);
    }

    @Override
    public List<UserDetail> toDomainList(List<UserDetailEntity> entities) {
        return List.of();
    }

    @Override
    public UserDetailEntity toEntity(UserDetail domain) {
        return EvoModelMapperUtils.toObject(domain, UserDetailEntity.class);
    }

    @Override
    public List<UserDetailEntity> toEntityList(List<UserDetail> domains) {
        return List.of();
    }
}
