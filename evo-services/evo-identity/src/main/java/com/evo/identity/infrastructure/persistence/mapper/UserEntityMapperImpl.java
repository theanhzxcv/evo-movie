package com.evo.identity.infrastructure.persistence.mapper;

import com.evo.identity.domain.User;
import com.evo.identity.infrastructure.persistence.entities.UserEntity;
import com.evo.support.DomainEntityMapper;
import com.evo.util.EvoModelMapperUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserEntityMapperImpl implements DomainEntityMapper<User, UserEntity> {

    @Override
    public User toDomain(UserEntity entity) {
        return EvoModelMapperUtils.toObject(entity, User.class);
    }

    @Override
    public List<User> toDomainList(List<UserEntity> entities) {
        return List.of();
    }

    @Override
    public UserEntity toEntity(User domain) {
        return EvoModelMapperUtils.toObject(domain, UserEntity.class);
    }

    @Override
    public List<UserEntity> toEntityList(List<User> domains) {
        return List.of();
    }
}
