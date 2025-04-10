package com.evo.identity.application.service.impl;

import com.evo.constans.ErrConstans;
import com.evo.exception.AppException;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.application.model.PermissionReqModel;
import com.evo.identity.application.model.PermissionResModel;
import com.evo.identity.application.model.PermissionSearchReqModel;
import com.evo.identity.application.model.PermissionSearchResModel;
import com.evo.identity.application.service.PermissionService;
import com.evo.identity.domain.Permission;
import com.evo.identity.domain.command.PermissionCmd;
import com.evo.identity.domain.query.PermissionQuery;
import com.evo.identity.domain.repository.PermissionDomainRepository;
import com.evo.identity.infrastructure.persistence.mapper.PermissionEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.PermissionEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.PermissionEntityRepositoryCustom;
import com.evo.util.EvoModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionEntityMapperImpl permissionEntityMapper;
    private final PermissionDomainRepository permissionDomainRepository;
    private final PermissionEntityRepository permissionEntityRepository;
    private final PermissionEntityRepositoryCustom permissionEntityRepositoryCustom;

    @Override
    public Map<String, Long> create(PermissionReqModel model) {
        if (permissionEntityRepository.findByNameAndIsActive(model.getName(), EActive.ACTIVE.value).isPresent()) {
            throw new AppException(ErrConstans.PERMISSION_DETAIL_ERROR_002);
        }

        PermissionCmd cmd = EvoModelMapperUtils.toObject(model, PermissionCmd.class);
        Permission permission = new Permission(cmd);
        permissionDomainRepository.save(permission);

        Map<String, Long> res = new HashMap<>();
        res.put("success", 1L);

        return res;
    }

    @Override
    public PermissionResModel update(UUID id, PermissionReqModel model) {
        Permission permission = permissionDomainRepository.getById(id);
        if (Objects.equals(EActive.INACTIVE.value, permission.getIsActive())) {
            throw new AppException(ErrConstans.PERMISSION_ERROR_001);
        }

        PermissionCmd cmd = EvoModelMapperUtils.toObject(model, PermissionCmd.class);
        permission.update(cmd);
        permissionDomainRepository.save(permission);

        return EvoModelMapperUtils.toObject(permission, PermissionResModel.class);
    }

    @Override
    public PermissionResModel delete(UUID id) {
        Permission permission = permissionDomainRepository.getById(id);
        if (Objects.equals(EActive.INACTIVE.value, permission.getIsActive())) {
            throw new AppException(ErrConstans.PERMISSION_ERROR_001);
        }
        permission.delete();
        permissionDomainRepository.save(permission);

        return EvoModelMapperUtils.toObject(permission, PermissionResModel.class);
    }

    @Override
    public PermissionResModel restore(UUID id) {
        Permission permission = permissionDomainRepository.getById(id);
        if (Objects.equals(EActive.ACTIVE.value, permission.getIsActive())) {
            throw new AppException(ErrConstans.PERMISSION_ERROR_002);
        }
        permission.restore();
        permissionDomainRepository.save(permission);

        return EvoModelMapperUtils.toObject(permission, PermissionResModel.class);
    }

    @Override
    public Page<PermissionSearchResModel> search(PermissionSearchReqModel model) {
        PermissionQuery query = EvoModelMapperUtils.toObject(model, PermissionQuery.class);
        long total = permissionEntityRepositoryCustom.countPermissions(query);
        if (total == 0) {
            return Page.empty();
        }

        List<Permission> permissions = permissionEntityRepositoryCustom.searchPermissions(query).stream()
                .map(permissionEntityMapper::toDomain)
                .toList();
        List<PermissionSearchResModel> resModels = EvoModelMapperUtils.listObjectToListModel(permissions, PermissionSearchResModel.class);

        return new PageImpl<>(resModels, PageRequest.of(query.getPageIndex(), query.getPageSize()), total);
    }

    @Override
    public PermissionResModel details(UUID id) {
        Permission permission = permissionDomainRepository.getById(id);
        if (Objects.equals(EActive.INACTIVE.value, permission.getIsActive())) {
            throw new AppException(ErrConstans.PERMISSION_ERROR_001);
        }

        return EvoModelMapperUtils.toObject(permission, PermissionResModel.class);
    }
}
