package com.evo.identity.application.service.impl;

import com.evo.constants.ErrConstants;
import com.evo.exception.AppException;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.application.model.AssignPermissionResModel;
import com.evo.identity.application.model.RoleDeleteReqModel;
import com.evo.identity.application.model.RoleDetailResModel;
import com.evo.identity.application.model.RoleReqModel;
import com.evo.identity.application.model.RoleResModel;
import com.evo.identity.application.model.RoleSearchReqModel;
import com.evo.identity.application.model.RoleSearchResModel;
import com.evo.identity.application.service.RoleService;
import com.evo.identity.domain.Role;
import com.evo.identity.domain.RolePermission;
import com.evo.identity.domain.command.RoleCmd;
import com.evo.identity.domain.command.RolePermissionCmd;
import com.evo.identity.domain.query.RoleQuery;
import com.evo.identity.domain.repository.RoleDomainRepository;
import com.evo.identity.infrastructure.persistence.entities.RoleEntity;
import com.evo.identity.infrastructure.persistence.mapper.RoleEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.RolePermissionEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserRoleEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.RoleEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.RoleEntityRepositoryCustom;
import com.evo.identity.infrastructure.persistence.repository.RolePermissionEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserEntityRepositoryCustom;
import com.evo.identity.infrastructure.persistence.repository.UserRoleEntityRepository;
import com.evo.util.EvoModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleEntityRepository roleEntityRepository;
    private final RoleDomainRepository roleDomainRepository;
    private final RoleEntityRepositoryCustom roleEntityRepositoryCustom;
    private final UserEntityRepositoryCustom userEntityRepositoryCustom;
    private final UserRoleEntityRepository userRoleEntityRepository;
    private final RolePermissionEntityRepository rolePermissionEntityRepository;

    private final RoleEntityMapperImpl roleEntityMapper;
    private final UserEntityMapperImpl userEntityMapper;
    private final UserRoleEntityMapperImpl userRoleEntityMapper;
    private final RolePermissionEntityMapperImpl rolePermissionEntityMapper;

    @Override
    public Map<String, Long> create(RoleReqModel model) {
        if (roleEntityRepository.findByNameAndIsActive(model.getName(), EActive.ACTIVE.value).isPresent()) {
            throw new AppException(ErrConstants.ROLE_DETAIL_ERROR_002);
        }

        RoleCmd roleCmd = EvoModelMapperUtils.toObject(model, RoleCmd.class);
        roleCmd.setRolePermissionCmds(buildRolePermissionCmds(model.getPermissionIds()));
        Role role = new Role(roleCmd);
        roleDomainRepository.save(role);

        Map<String, Long> res = new HashMap<>();
        res.put("success", 1L);

        return res;
    }

    @Override
    public RoleResModel update(UUID id, RoleReqModel model) {
        Role role = findRole(id);
        if (Objects.equals(EActive.INACTIVE.value, role.getIsActive())) {
            throw new AppException(ErrConstants.ROLE_ERROR_001);
        }

        RoleCmd roleCmd = EvoModelMapperUtils.toObject(model, RoleCmd.class);
        roleCmd.setRolePermissionCmds(buildRolePermissionCmds(model.getPermissionIds()));
        role.update(roleCmd);
        roleDomainRepository.save(role);

        RoleResModel res = EvoModelMapperUtils.toObject(role, RoleResModel.class);
        res.setPermissionIds(role.getRolePermissions().stream()
                .filter(rp -> Objects.equals(EActive.ACTIVE.value, rp.getIsActive()))
                .map(RolePermission::getPermissionId)
                .toList());

        return res;
    }

    @Override
    public RoleResModel delete(RoleDeleteReqModel model) {
        Role role = findRole(model.getCurrentId());
        if (Objects.equals(EActive.INACTIVE.value, role.getIsActive())) {
            throw new AppException(ErrConstants.ROLE_ERROR_001);
        }
//        Long assignedUserCount = userEntityRepositoryCustom.countUsersByRoleId(role.getId());
//        if (assignedUserCount > 0) {
//            Role newRole = findRole(model.getNewId());
//            if (Objects.equals(EActive.INACTIVE.value, newRole.getIsActive())) {
//                throw new AppException(ErrConstans.ROLE_ERROR_001);
//            }
//
//            if (Objects.equals(newRole.getId(), model.getCurrentId())) {
//                throw new AppException(ErrConstans.CHANGE_ROLE_ERROR_001);
//            }
//
//            List<UserEntity> userEntities = userEntityRepositoryCustom.searchUsersByRoleId(role.getId());
//            List<UserRole> userRoles = userRoleEntityRepository.findByUserIdIn(userEntities.stream()
//                    .map(UserEntity::getId).toList()).stream()
//                    .map(userRoleEntityMapper::toDomain)
//                    .toList();
//            for (UserRole userRole : userRoles) {
//                userRole
//            }
//
//        }
        role.delete();
        roleDomainRepository.save(role);

        return EvoModelMapperUtils.toObject(role, RoleResModel.class);
    }

    @Override
    public RoleResModel restore(UUID id) {
        Role role = findRole(id);
        if (Objects.equals(EActive.ACTIVE.value, role.getIsActive())) {
            throw new AppException(ErrConstants.ROLE_ERROR_002);
        }
        role.restore();
        roleDomainRepository.save(role);

        RoleResModel res = EvoModelMapperUtils.toObject(role, RoleResModel.class);
        List<UUID> permissionIds = role.getRolePermissions().stream()
                .filter(rp -> Objects.equals(EActive.ACTIVE.value, rp.getIsActive()))
                .map(RolePermission::getPermissionId)
                .toList();
        res.setPermissionIds(permissionIds);

        return res;
    }

    @Override
    public Page<RoleSearchResModel> search(RoleSearchReqModel model) {
        RoleQuery query = EvoModelMapperUtils.toObject(model, RoleQuery.class);
        long total = roleEntityRepositoryCustom.countRoles(query);
        if (total == 0) {
            return Page.empty();
        }

        List<Role> roles = roleEntityRepositoryCustom.searchRoles(query).stream()
                .map(roleEntityMapper::toDomain)
                .toList();
        List<RoleSearchResModel> resModels = EvoModelMapperUtils.listObjectToListModel(roles, RoleSearchResModel.class);

        return new PageImpl<>(resModels, PageRequest.of(query.getPageIndex(), query.getPageSize()), total);
    }

    @Override
    public RoleDetailResModel details(UUID id) {
        Role role = findRole(id);
        if (Objects.equals(EActive.INACTIVE.value, role.getIsActive())) {
            throw new AppException(ErrConstants.ROLE_ERROR_001);
        }

        Long assignedUserCount = userEntityRepositoryCustom.countUsersByRoleId(id);
        List<AssignPermissionResModel> models = roleEntityRepositoryCustom.getAssignPermissionsByRoleId(id);
        RoleDetailResModel res = EvoModelMapperUtils.toObject(role, RoleDetailResModel.class);
        res.setAssignedUserCount(assignedUserCount);
        res.setAssignPermissions(models);

        return res;
    }

    @Override
    public List<AssignPermissionResModel> getAssignPermissions(UUID roleId) {
        return roleEntityRepositoryCustom.getAssignPermissionsByRoleId(roleId);
    }

    private Role findRole(UUID id) {
        RoleEntity roleEntity = roleEntityRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrConstants.ROLE_DETAIL_ERROR_001));
        Role role = roleEntityMapper.toDomain(roleEntity);
        role.enrichRolePermission(fetchExistingPermission(role.getId()));
        return role;
    }

    private List<RolePermission> fetchExistingPermission(UUID roleId) {
        return rolePermissionEntityRepository.findAllByRoleIdAndIsActive(roleId, EActive.ACTIVE.value)
                .stream()
                .map(rolePermissionEntityMapper::toDomain)
                .toList();
    }

    private List<RolePermissionCmd> buildRolePermissionCmds(List<UUID> permissionIds) {
        List<RolePermissionCmd> rolePermissionCmds = new ArrayList<>();
        for (UUID permissionId : permissionIds) {
            RolePermissionCmd rolePermissionCmd = new RolePermissionCmd();
            rolePermissionCmd.setPermissionId(permissionId);
            rolePermissionCmds.add(rolePermissionCmd);
        }
        return rolePermissionCmds;
    }
}
