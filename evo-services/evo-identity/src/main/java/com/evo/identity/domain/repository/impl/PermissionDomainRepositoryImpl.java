package com.evo.identity.domain.repository.impl;

import com.evo.constans.ErrConstans;
import com.evo.exception.AppException;
import com.evo.identity.domain.Permission;
import com.evo.identity.domain.repository.PermissionDomainRepository;
import com.evo.identity.infrastructure.persistence.entities.PermissionEntity;
import com.evo.identity.infrastructure.persistence.mapper.PermissionEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.PermissionEntityRepository;
import com.evo.support.AbstractDomainRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class PermissionDomainRepositoryImpl
        extends AbstractDomainRepository<Permission, PermissionEntity, UUID>
        implements PermissionDomainRepository {

    private final PermissionEntityMapperImpl permissionEntityMapper;
    private final PermissionEntityRepository permissionEntityRepository;

    protected PermissionDomainRepositoryImpl(PermissionEntityRepository permissionEntityRepository,
                                             PermissionEntityMapperImpl permissionEntityMapper) {
        super(permissionEntityRepository, permissionEntityMapper);
        this.permissionEntityMapper = permissionEntityMapper;
        this.permissionEntityRepository = permissionEntityRepository;
    }

    @Override
    public Permission save(Permission permission) {
        PermissionEntity permissionEntity = permissionEntityMapper.toEntity(permission);
        permissionEntityRepository.save(permissionEntity);
        return permission;
    }

    @Override
    public Permission getById(UUID id) {
        return this.findById(id).orElseThrow(() -> new AppException(ErrConstans.PERMISSION_DETAIL_ERROR_001));
    }

    @Override
    public void existsById(UUID id) {
        if (this.findById(id).isPresent()) {
            throw new AppException(ErrConstans.PERMISSION_DETAIL_ERROR_002);
        }
    }
}
