package com.evo.identity.infrastructure.persistence.mapper;

import com.evo.identity.domain.UserActivity;
import com.evo.identity.infrastructure.persistence.entities.UserActivityEntity;
import com.evo.support.DomainEntityMapper;
import com.evo.util.EvoModelMapperUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserActivityEntityMapperImpl implements DomainEntityMapper<UserActivity, UserActivityEntity> {

    @Override
    public UserActivity toDomain(UserActivityEntity entity) {
        return EvoModelMapperUtils.toObject(entity, UserActivity.class);
    }

    @Override
    public List<UserActivity> toDomainList(List<UserActivityEntity> entities) {
        return List.of();
    }

    @Override
    public UserActivityEntity toEntity(UserActivity domain) {
        return EvoModelMapperUtils.toObject(domain, UserActivityEntity.class);
    }

    @Override
    public List<UserActivityEntity> toEntityList(List<UserActivity> domains) {
        return List.of();
    }
}
