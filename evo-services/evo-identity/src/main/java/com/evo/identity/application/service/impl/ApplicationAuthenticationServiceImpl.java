package com.evo.identity.application.service.impl;

import com.evo.constants.ErrConstants;
import com.evo.exception.AppException;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.application.enums.EDefaultRole;
import com.evo.identity.application.model.AuthenticationReqModel;
import com.evo.identity.application.model.AuthenticationResModel;
import com.evo.identity.application.model.RegistrationReqModel;
import com.evo.identity.application.security.JwtUtils;
import com.evo.identity.application.service.AuthenticationService;
import com.evo.identity.domain.Role;
import com.evo.identity.domain.User;
import com.evo.identity.domain.command.TokenInfoCmd;
import com.evo.identity.domain.command.UserDetailCmd;
import com.evo.identity.domain.command.UserRegistrationCmd;
import com.evo.identity.domain.command.UserRoleCmd;
import com.evo.identity.domain.repository.UserDomainRepository;
import com.evo.identity.infrastructure.persistence.entities.UserEntity;
import com.evo.identity.infrastructure.persistence.mapper.RoleEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.RoleEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserDetailEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserEntityRepository;
import com.evo.util.EvoModelMapperUtils;
import lombok.RequiredArgsConstructor;
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
public class ApplicationAuthenticationServiceImpl implements AuthenticationService {

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepository userEntityRepository;
    private final UserDomainRepository userDomainRepository;
    private final UserDetailEntityRepository userDetailEntityRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final RoleEntityMapperImpl roleEntityMapper;

    @Override
    public AuthenticationResModel signIn(AuthenticationReqModel model) {
        User user = findUser(model.getUserName());

        if (Objects.equals(EActive.INACTIVE.value, user.getIsActive())) {
            throw new AppException(ErrConstants.AUTH_ERROR_001);
        }

        if (!passwordEncoder.matches(model.getUserPass(), user.getUserPass())) {
            throw new AppException(ErrConstants.AUTH_ERROR_002);
        }

        try {
            String accessToken = jwtUtils.generateAccessToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(user);
            Long accessTokenExpireAt = jwtUtils.getExpirationTime(accessToken);
            Long refreshTokenExpireAt = jwtUtils.getExpirationTime(refreshToken);


            TokenInfoCmd cmd = new TokenInfoCmd();
            cmd.setUserId(user.getId());
            cmd.setAccessToken(accessToken);
            cmd.setRefreshToken(refreshToken);
            cmd.setAccessTokenExpireAt(accessTokenExpireAt);
            cmd.setRefreshTokenExpireAt(refreshTokenExpireAt);
            user.saveTokenInfo(cmd);

            userDomainRepository.save(user);

            AuthenticationResModel res = new AuthenticationResModel();
            res.setAccessToken(accessToken);
            res.setRefreshToken(refreshToken);
            res.setAccessTokenExpireAt(accessTokenExpireAt);
            res.setRefreshTokenExpireAt(refreshTokenExpireAt);

            return res;
        } catch (Exception e) {
            throw new AppException(ErrConstants.SYSTEM_ERROR_001);
        }
    }

    @Override
    public Map<String, Long> signUp(RegistrationReqModel model) {
        if (userEntityRepository.findByUserNameAndIsActive(model.getUserName(), EActive.ACTIVE.value).isPresent()) {
            throw new AppException(ErrConstants.USER_DETAIL_ERROR_002);
        }

        if (userDetailEntityRepository.findByEmail(model.getUserEmail()).isPresent()) {
            throw new AppException(ErrConstants.USER_DETAIL_ERROR_003);
        }

        try {
            UserRegistrationCmd registrationCmd = EvoModelMapperUtils.toObject(model, UserRegistrationCmd.class);
            registrationCmd.setUserPass(passwordEncoder.encode(registrationCmd.getUserPass()));
            UserDetailCmd userDetailCmd = new UserDetailCmd();
            userDetailCmd.setFirstName(model.getFirstName());
            userDetailCmd.setLastName(model.getLastName());
            userDetailCmd.setEmail(model.getUserEmail());
            registrationCmd.setUserDetailCmd(userDetailCmd);
            Role role = roleEntityRepository.findByNameAndIsActive(EDefaultRole.USER.value, EActive.ACTIVE.value)
                    .map(roleEntityMapper::toDomain)
                    .orElseThrow(() -> new AppException(ErrConstants.ROLE_DETAIL_ERROR_001));
            registrationCmd.setUserRoleCmds(buildUserRoleCmds(role.getId()));
//            keycloakUtils.registrationWithKeycloak(registrationCmd);
            registrationCmd.setUserPass(passwordEncoder.encode(model.getUserPass()));
            User user = new User(registrationCmd);
            userDomainRepository.save(user);

            /**
             * Assign role
             */
            userDomainRepository.save(user);

            Map<String, Long> res = new HashMap<>();
            res.put("status", 1L);
            return res;
        } catch (Exception e){
            throw new AppException(ErrConstants.SYSTEM_ERROR_001);
        }
    }

    @Override
    public AuthenticationResModel tfaRequired() {
        return null;
    }

    private User findUser(String userName) {
        UserEntity userEntity = userEntityRepository.findByUserNameAndIsActive(userName, EActive.ACTIVE.value)
                .orElseThrow(() -> new AppException(ErrConstants.USER_DETAIL_ERROR_001));

        return EvoModelMapperUtils.toObject(userEntity, User.class);
    }

    private List<UserRoleCmd> buildUserRoleCmds(UUID roleId) {
        List<UserRoleCmd> userRoleCmds = new ArrayList<>();
        UserRoleCmd userRoleCmd = new UserRoleCmd();
        userRoleCmd.setRoleId(roleId);
        userRoleCmds.add(userRoleCmd);
        return userRoleCmds;
    }
}
