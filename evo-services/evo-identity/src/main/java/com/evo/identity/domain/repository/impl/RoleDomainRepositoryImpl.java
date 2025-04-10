package com.evo.identity.domain.repository.impl;

import com.evo.constans.ErrConstans;
import com.evo.exception.AppException;
import com.evo.identity.domain.Role;
import com.evo.identity.domain.repository.RoleDomainRepository;
import com.evo.identity.infrastructure.persistence.entities.RoleEntity;
import com.evo.identity.infrastructure.persistence.entities.RolePermissionEntity;
import com.evo.identity.infrastructure.persistence.mapper.RoleEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.RolePermissionEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.RoleEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.RolePermissionEntityRepository;
import com.evo.support.AbstractDomainRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Repository
public class RoleDomainRepositoryImpl
        extends AbstractDomainRepository<Role, RoleEntity, UUID>
        implements RoleDomainRepository {

    private final RoleEntityMapperImpl roleEntityMapper;
    private final RolePermissionEntityMapperImpl rolePermissionEntityMapper;
    private final RoleEntityRepository roleEntityRepository;
    private final RolePermissionEntityRepository rolePermissionEntityRepository;

    protected RoleDomainRepositoryImpl(RoleEntityRepository roleEntityRepository,
                                       RolePermissionEntityRepository rolePermissionEntityRepository,
                                       RoleEntityMapperImpl roleEntityMapper,
                                       RolePermissionEntityMapperImpl rolePermissionEntityMapper) {
        super(roleEntityRepository, roleEntityMapper);
        this.roleEntityMapper = roleEntityMapper;
        this.rolePermissionEntityMapper = rolePermissionEntityMapper;
        this.roleEntityRepository = roleEntityRepository;
        this.rolePermissionEntityRepository = rolePermissionEntityRepository;
    }

    @Override
    public Role save(Role role) {
        RoleEntity roleEntity = roleEntityMapper.toEntity(role);
        roleEntityRepository.save(roleEntity);

        if (!CollectionUtils.isEmpty(role.getRolePermissions())) {
            List<RolePermissionEntity> rolePermissionEntities = role.getRolePermissions().stream()
                    .map(rolePermissionEntityMapper::toEntity)
                    .toList();
            rolePermissionEntityRepository.saveAll(rolePermissionEntities);
        }

        return role;
    }

    @Override
    public Role getById(UUID id) {
        return this.findById(id).orElseThrow(() -> new AppException(ErrConstans.ROLE_DETAIL_ERROR_001));
    }

    @Override
    public void existsById(UUID uuid) {

    }
}
