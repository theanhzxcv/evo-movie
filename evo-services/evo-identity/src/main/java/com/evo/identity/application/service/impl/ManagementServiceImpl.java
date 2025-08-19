package com.evo.identity.application.service.impl;

import com.evo.constants.ErrConstants;
import com.evo.exception.AppException;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.application.enums.EResponseStatus;
import com.evo.identity.application.model.AssignRoleResModel;
import com.evo.identity.application.model.ProfileResModel;
import com.evo.identity.application.model.UserDetailResModel;
import com.evo.identity.application.model.UserReqModel;
import com.evo.identity.application.model.UserResModel;
import com.evo.identity.application.model.UserSearchReqModel;
import com.evo.identity.application.model.UserSearchResModel;
import com.evo.identity.application.service.ManagementService;
import com.evo.identity.domain.User;
import com.evo.identity.domain.UserDetail;
import com.evo.identity.domain.UserRole;
import com.evo.identity.domain.command.UserCmd;
import com.evo.identity.domain.command.UserDetailCmd;
import com.evo.identity.domain.command.UserRegistrationCmd;
import com.evo.identity.domain.command.UserRoleCmd;
import com.evo.identity.domain.query.UserQuery;
import com.evo.identity.domain.repository.UserDomainRepository;
import com.evo.identity.infrastructure.adapter.keycloak.KeycloakUtils;
import com.evo.identity.infrastructure.persistence.entities.UserEntity;
import com.evo.identity.infrastructure.persistence.mapper.UserDetailEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserRoleEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.UserDetailEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserRoleEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.custom.UserEntityRepositoryCustom;
import com.evo.security.validation.ValidationService;
import com.evo.util.EvoModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManagementServiceImpl implements ManagementService {

    private final KeycloakUtils keycloakUtils;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final UserEntityRepository userEntityRepository;
    private final UserDomainRepository userDomainRepository;
    private final UserDetailEntityRepository userDetailEntityRepository;
    private final UserEntityRepositoryCustom userEntityRepositoryCustom;
    private final UserRoleEntityRepository userRoleEntityRepository;
    private final UserEntityMapperImpl userEntityMapper;
    private final UserRoleEntityMapperImpl userRoleEntityMapper;
    private final UserDetailEntityMapperImpl userDetailEntityMapper;

    @Override
    public Map<String, Long> create(UserReqModel model) {

        if (userDetailEntityRepository.findByEmail(model.getUserEmail()).isPresent()) {
            throw new AppException(ErrConstants.USER_DETAIL_ERROR_003);
        }

        UserCmd userCreateCmd = EvoModelMapperUtils.toObject(model, UserCmd.class);
        UserDetailCmd userDetailCmd = new UserDetailCmd();
        userDetailCmd.setFirstName(model.getFirstName());
        userDetailCmd.setLastName(model.getLastName());
        userDetailCmd.setEmail(model.getUserEmail());
        userCreateCmd.setUserDetailCmd(userDetailCmd);
        userCreateCmd.setUserRoleCmds(buildUserRoleCmds(model.getRoleIds()));
        UserRegistrationCmd userRegistrationCmd = EvoModelMapperUtils.toObject(userCreateCmd, UserRegistrationCmd.class);
        keycloakUtils.registrationWithKeycloak(userRegistrationCmd);
        userCreateCmd.setUserPass(passwordEncoder.encode(model.getUserPass()));
        User user = new User(userCreateCmd);
        userDomainRepository.save(user);

        Map<String, Long> res = new HashMap<>();
        res.put("status", EResponseStatus.SUCCESS.value);

        return res;
    }

    @Override
    public UserResModel update(UUID id, UserReqModel model) {
        User user = findUser(id);

        UserCmd userUpdateCmd = EvoModelMapperUtils.toObject(model, UserCmd.class);
        userUpdateCmd.setUserPass(passwordEncoder.encode(model.getUserPass()));
        UserDetailCmd userDetailCmd = EvoModelMapperUtils.toObject(user.getUserDetail(), UserDetailCmd.class);
        userDetailCmd.setFirstName(model.getFirstName());
        userDetailCmd.setLastName(model.getLastName());
        userDetailCmd.setEmail(model.getUserEmail());
        userUpdateCmd.setUserDetailCmd(userDetailCmd);
        userUpdateCmd.setUserRoleCmds(buildUserRoleCmds(model.getRoleIds()));
        keycloakUtils.updateKeycloakUser(user.getUserDetail().getEmail(), userUpdateCmd);
        user.update(userUpdateCmd);
        userDomainRepository.save(user);

        UserResModel res = EvoModelMapperUtils.toObject(user, UserResModel.class);
        res.setProfile(EvoModelMapperUtils.toObject(user.getUserDetail(), ProfileResModel.class));
        res.setAssignRoles(userEntityRepositoryCustom.getAssignRolesByUserId(user.getId()));

        return res;
    }

    @Override
    public UserResModel delete(UUID id) {
        User user = findUser(id);
        if (Objects.equals(EActive.INACTIVE.value, user.getIsActive())) {
            throw new AppException(ErrConstants.USER_ERROR_001);
        }

        user.delete();
        userDomainRepository.save(user);

        return EvoModelMapperUtils.toObject(user, UserResModel.class);
    }

    @Override
    public UserResModel restore(UUID id) {
        User user = findUser(id);
        if (Objects.equals(EActive.ACTIVE.value, user.getIsActive())) {
            throw new AppException(ErrConstants.USER_ERROR_002);
        }

        user.restore();
        userDomainRepository.save(user);

        return EvoModelMapperUtils.toObject(user, UserResModel.class);
    }

    @Override
    public Page<UserSearchResModel> search(UserSearchReqModel model) {
        UserQuery query = EvoModelMapperUtils.toObject(model, UserQuery.class);
        long total = userEntityRepositoryCustom.countUsers(query);
        if (total == 0) {
            return Page.empty();
        }

        List<User> users = userEntityRepositoryCustom.searchUsers(query).stream()
                .map(userEntityMapper::toDomain)
                .toList();
        List<UserSearchResModel> resModels = EvoModelMapperUtils.listObjectToListModel(users, UserSearchResModel.class);

        return new PageImpl<>(resModels, PageRequest.of(query.getPageIndex(), query.getPageSize()), total);
    }

    @Override
    public UserDetailResModel details(UUID id) {
        User user = findUser(id);
        if (Objects.equals(EActive.INACTIVE.value, user.getIsActive())) {
            throw new AppException(ErrConstants.ROLE_ERROR_001);
        }

        List<AssignRoleResModel> models = userEntityRepositoryCustom.getAssignRolesByUserId(id);
        UserDetailResModel res = EvoModelMapperUtils.toObject(user, UserDetailResModel.class);
        res.setAssignRoles(models);

        return res;
    }

    private User findUser(UUID userId) {
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrConstants.USER_DETAIL_ERROR_001));
        User user = userEntityMapper.toDomain(userEntity);
        user.enrichUserRole(fetchExistingRole(user.getId()));
        user.enrichUserDetail(fetchExistingUserDetail(user.getId()));

        return user;
    }

    private UserDetail fetchExistingUserDetail(UUID userId) {
        return userDetailEntityRepository.findByUserId(userId)
                .map(userDetailEntityMapper::toDomain)
                .orElseThrow(() -> new AppException(ErrConstants.USER_DETAIL_ERROR_001));
    }

    private List<UserRole> fetchExistingRole(UUID userId) {
        return userRoleEntityRepository.findAllByUserIdAndIsActive(userId, EActive.ACTIVE.value)
                .stream()
                .map(userRoleEntityMapper::toDomain)
                .toList();
    }

    private List<UserRoleCmd> buildUserRoleCmds(List<UUID> roleIds) {
        List<UserRoleCmd> userRoleCmds = new ArrayList<>();
        for (UUID roleId : roleIds) {
            UserRoleCmd userRoleCmd = new UserRoleCmd();
            userRoleCmd.setRoleId(roleId);
            userRoleCmds.add(userRoleCmd);
        }
        return userRoleCmds;
    }
}
