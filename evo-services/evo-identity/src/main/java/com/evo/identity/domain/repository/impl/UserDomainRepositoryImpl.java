package com.evo.identity.domain.repository.impl;

import com.evo.constans.ErrConstans;
import com.evo.exception.AppException;
import com.evo.identity.domain.User;
import com.evo.identity.domain.repository.UserDomainRepository;
import com.evo.identity.infrastructure.persistence.entities.TokenInfoEntity;
import com.evo.identity.infrastructure.persistence.entities.UserDetailEntity;
import com.evo.identity.infrastructure.persistence.entities.UserEntity;
import com.evo.identity.infrastructure.persistence.mapper.TokenInfoEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserDetailEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.TokenInfoEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserDetailEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserEntityRepository;
import com.evo.support.AbstractDomainRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
public class UserDomainRepositoryImpl
        extends AbstractDomainRepository<User, UserEntity, UUID>
        implements UserDomainRepository {

    private final UserEntityMapperImpl userEntityMapper;
    private final UserDetailEntityMapperImpl userDetailEntityMapper;
    private final TokenInfoEntityMapperImpl tokenInfoEntityMapper;

    private final UserEntityRepository userEntityRepository;
    private final UserDetailEntityRepository userDetailEntityRepository;
    private final TokenInfoEntityRepository tokenInfoEntityRepository;


    protected UserDomainRepositoryImpl(UserEntityRepository userEntityRepository,
                                       UserDetailEntityRepository userDetailEntityRepository,
                                       TokenInfoEntityRepository tokenInfoEntityRepository,
                                       UserEntityMapperImpl userEntityMapper,
                                       UserDetailEntityMapperImpl userDetailEntityMapper,
                                       TokenInfoEntityMapperImpl tokenInfoEntityMapper) {
        super(userEntityRepository, userEntityMapper);
        this.userEntityMapper = userEntityMapper;
        this.userDetailEntityMapper = userDetailEntityMapper;
        this.tokenInfoEntityMapper = tokenInfoEntityMapper;

        this.userEntityRepository = userEntityRepository;
        this.userDetailEntityRepository = userDetailEntityRepository;
        this.tokenInfoEntityRepository = tokenInfoEntityRepository;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userEntityMapper.toEntity(user);
        userEntityRepository.save(userEntity);

        if (!ObjectUtils.isEmpty(user.getUserDetail())) {
            UserDetailEntity userDetailEntity = userDetailEntityMapper.toEntity(user.getUserDetail());
            userDetailEntityRepository.save(userDetailEntity);
        }

        if (!ObjectUtils.isEmpty(user.getTokenInfo())) {
            TokenInfoEntity tokenInfoEntity = tokenInfoEntityMapper.toEntity(user.getTokenInfo());
            tokenInfoEntityRepository.save(tokenInfoEntity);
        }

        return user;
    }

    @Override
    public User getById(UUID id) {
        return this.findById(id).orElseThrow(() -> new AppException(ErrConstans.USER_DETAIL_ERROR_001));
    }

    @Override
    public void existsById(UUID id) {
        if (this.findById(id).isPresent()) {
            throw new AppException(ErrConstans.USER_DETAIL_ERROR_002);
        }
    }
}
