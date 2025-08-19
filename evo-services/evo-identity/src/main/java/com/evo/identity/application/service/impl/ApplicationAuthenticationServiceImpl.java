package com.evo.identity.application.service.impl;

import com.evo.constants.ErrConstants;
import com.evo.exception.AppException;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.application.enums.EActivityType;
import com.evo.identity.application.enums.EDefaultRole;
import com.evo.identity.application.enums.ELockThreshold;
import com.evo.identity.application.enums.EResponseStatus;
import com.evo.identity.application.enums.ETfaStatus;
import com.evo.identity.application.enums.ETokenExpiration;
import com.evo.identity.application.enums.ETokenType;
import com.evo.identity.application.enums.EVerify;
import com.evo.identity.application.model.AuthenticationReqModel;
import com.evo.identity.application.model.AuthenticationResModel;
import com.evo.identity.application.model.RegistrationReqModel;
import com.evo.identity.application.security.JwtUtils;
import com.evo.identity.application.service.AuthenticationService;
import com.evo.identity.domain.Role;
import com.evo.identity.domain.TokenInfo;
import com.evo.identity.domain.User;
import com.evo.identity.domain.UserActivity;
import com.evo.identity.domain.UserDetail;
import com.evo.identity.domain.command.TokenInfoCmd;
import com.evo.identity.domain.command.UserActivityCmd;
import com.evo.identity.domain.command.UserCmd;
import com.evo.identity.domain.command.UserDetailCmd;
import com.evo.identity.domain.command.UserRegistrationCmd;
import com.evo.identity.domain.command.UserRoleCmd;
import com.evo.identity.domain.repository.UserDomainRepository;
import com.evo.identity.infrastructure.adapter.tfa.TfaService;
import com.evo.identity.infrastructure.configuration.ApplicationProperties;
import com.evo.identity.infrastructure.persistence.mapper.RoleEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.TokenInfoEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserActivityEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserDetailEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.RoleEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.TokenInfoEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserActivityEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserDetailEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserEntityRepository;
import com.evo.security.validation.ValidationService;
import com.evo.security.validation.impl.BlacklistedTokenServiceImpl;
import com.evo.util.EvoDateUtils;
import com.evo.util.EvoEncryptionUtils;
import com.evo.util.EvoModelMapperUtils;
import com.evo.util.EvoSecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationAuthenticationServiceImpl implements AuthenticationService {

    private final JwtUtils jwtUtils;
    private final TfaService tfaService;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final BlacklistedTokenServiceImpl blacklistedTokenService;
    private final ApplicationProperties applicationProperties;
    private final UserEntityRepository userEntityRepository;
    private final UserDomainRepository userDomainRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final TokenInfoEntityRepository tokenInfoEntityRepository;
    private final UserDetailEntityRepository userDetailEntityRepository;
    private final UserActivityEntityRepository userActivityEntityRepository;
    private final UserEntityMapperImpl userEntityMapper;
    private final RoleEntityMapperImpl roleEntityMapper;
    private final TokenInfoEntityMapperImpl tokenInfoEntityMapper;
    private final UserDetailEntityMapperImpl userDetailEntityMapper;
    private final UserActivityEntityMapperImpl userActivityEntityMapper;

    @Override
    public AuthenticationResModel signIn(AuthenticationReqModel model) {
        User user = findUser(model.getUserName());

        if (Objects.equals(EActive.INACTIVE.value, user.getIsActive())) {
            throw new AppException(ErrConstants.AUTH_ERROR_001);
        }

        UserCmd userCmd = EvoModelMapperUtils.toObject(user, UserCmd.class);

        List<UserActivity> activitiesHistory =
                userActivityEntityRepository
                        .findByUserNameAndActivityOrderByCreatedAtDesc(model.getUserName(), EActivityType.LOGIN.value)
                        .stream().map(userActivityEntityMapper::toDomain)
                        .toList();

        UserActivity latestLoginActivity = null;
        UserActivityCmd latestLoginActivityCmd = null;
        boolean isLocked = false;
        if (!activitiesHistory.isEmpty()) {
            latestLoginActivity = activitiesHistory.getFirst();
            if (Objects.equals(EResponseStatus.FAILED.value, latestLoginActivity.getStatus())) {
                latestLoginActivityCmd = EvoModelMapperUtils.toObject(latestLoginActivity, UserActivityCmd.class);
                user.enrichUserActivity(latestLoginActivity);
                if (Objects.equals(ELockThreshold.THRESHOLD_LOCK.value, latestLoginActivity.getRetryCount())) {
                    isLocked = true;
                }
            }
        }

        if (isLocked) {
            if (latestLoginActivity.getLockUntil().before(new Date())) {
                throw new AppException(ErrConstants.LOGIN_ERROR_003);
            } else {
                latestLoginActivityCmd.setRetryCount(ELockThreshold.RESET.value);
                latestLoginActivityCmd.setLockUntil(null);
                userCmd.setUserActivityCmd(latestLoginActivityCmd);
            }
        }

        if (!passwordEncoder.matches(model.getUserPass(), user.getUserPass())) {
            if (latestLoginActivityCmd != null) {
                Long failedAttempts = latestLoginActivityCmd.getRetryCount() + ELockThreshold.THRESHOLD_MIN.value;
                latestLoginActivityCmd.setRetryCount(failedAttempts);

                if (Objects.equals(failedAttempts, ELockThreshold.THRESHOLD_LOCK.value)) {
                    latestLoginActivityCmd.setLockUntil(
                            EvoDateUtils.addMinute(new Date(), ELockThreshold.LOCK_DURATION.value.intValue())
                    );
                    userCmd.setUserActivityCmd(latestLoginActivityCmd);
                    user.update(userCmd);
                    userDomainRepository.save(user);
                    throw new AppException(ErrConstants.LOGIN_ERROR_003);
                }
                userCmd.setUserActivityCmd(latestLoginActivityCmd);
                user.update(userCmd);
                userDomainRepository.save(user);
                if (Objects.equals(failedAttempts, ELockThreshold.THRESHOLD_WARN_HIGH.value)
                        || Objects.equals(failedAttempts, ELockThreshold.THRESHOLD_WAIT_LOCK.value)) {
                    throw new AppException(ErrConstants.LOGIN_ERROR_002);
                } else {
                    throw new AppException(ErrConstants.LOGIN_ERROR_001);
                }
            }else {
                latestLoginActivityCmd = new UserActivityCmd();
                latestLoginActivityCmd.setUserName(user.getUserName());
                latestLoginActivityCmd.setRetryCount(ELockThreshold.THRESHOLD_MIN.value);
                latestLoginActivityCmd.setLockUntil(null);
                latestLoginActivityCmd.setStatus(EResponseStatus.FAILED.value);
                latestLoginActivityCmd.setActivity(EActivityType.LOGIN.value);
                userCmd.setUserActivityCmd(latestLoginActivityCmd);
                user.update(userCmd);

                userDomainRepository.save(user);
                throw new AppException(ErrConstants.LOGIN_ERROR_001);
            }
        }

        try {
            String accessToken = jwtUtils.generateAccessToken(user);
            Long accessTokenExpireAt = jwtUtils.getExpirationTime(accessToken);
            String refreshToken = jwtUtils.generateRefreshToken(user);
            Long refreshTokenExpireAt = jwtUtils.getExpirationTime(refreshToken);

            TokenInfoCmd tokenInfoCmd = new TokenInfoCmd();
            tokenInfoCmd.setUserId(user.getId());

            if (Objects.equals(ETfaStatus.ENABLED.value, user.getIsTfaEnabled())) {
                String tfaToken = jwtUtils.generateToken(user, ETokenExpiration.TFA_TOKEN.value);
                Long tfaTokenExpireAt = jwtUtils.getExpirationTime(tfaToken);

                tokenInfoCmd.setAccessToken(jwtUtils.getTokenJti(tfaToken));
                tokenInfoCmd.setAccessTokenExpireAt(tfaTokenExpireAt);
                tokenInfoCmd.setType(ETokenType.TFA.value);
                userCmd.setTokenInfoCmd(tokenInfoCmd);
                user.update(userCmd);

                userDomainRepository.save(user);

                AuthenticationResModel tfaRes = new AuthenticationResModel();
                tfaRes.setAccessToken(tfaToken);
                tfaRes.setAccessTokenExpireAt(tfaTokenExpireAt);
                tfaRes.setTokenType(ETokenType.TFA.value);
                tfaRes.setIsTfaEnabled(user.getIsTfaEnabled());
                tfaRes.setSecretKey(user.getSecretKey());

                return tfaRes;
            }

            tokenInfoCmd.setAccessToken(jwtUtils.getTokenJti(accessToken));
            tokenInfoCmd.setRefreshToken(jwtUtils.getTokenJti(refreshToken));
            tokenInfoCmd.setAccessTokenExpireAt(accessTokenExpireAt);
            tokenInfoCmd.setRefreshTokenExpireAt(refreshTokenExpireAt);
            tokenInfoCmd.setType(ETokenType.ACCESS.value);
            userCmd.setTokenInfoCmd(tokenInfoCmd);

            UserActivityCmd userActivityCmd = new UserActivityCmd();
            userActivityCmd.setUserName(user.getUserName());
            userActivityCmd.setRetryCount(ELockThreshold.RESET.value);
            userActivityCmd.setLockUntil(null);
            userActivityCmd.setStatus(EResponseStatus.SUCCESS.value);
            userActivityCmd.setActivity(EActivityType.LOGIN.value);
            userCmd.setUserActivityCmd(userActivityCmd);
            user.update(userCmd);

            userDomainRepository.save(user);

            AuthenticationResModel res = new AuthenticationResModel();
            res.setAccessToken(accessToken);
            res.setRefreshToken(refreshToken);
            res.setAccessTokenExpireAt(accessTokenExpireAt);
            res.setRefreshTokenExpireAt(refreshTokenExpireAt);
            res.setTokenType(ETokenType.ACCESS.value);
            res.setIsTfaEnabled(user.getIsTfaEnabled());
            res.setSecretKey(user.getSecretKey());

            return res;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrConstants.SYSTEM_ERROR_001);
        }
    }

    @Override
    public Map<String, UUID> signUp(RegistrationReqModel model) {
        if (userEntityRepository.findByUserNameAndIsActive(model.getUserName(), EActive.ACTIVE.value).isPresent()) {
            throw new AppException(ErrConstants.USER_DETAIL_ERROR_002);
        }

        if (userDetailEntityRepository.findByEmail(model.getEmail()).isPresent()) {
            throw new AppException(ErrConstants.USER_DETAIL_ERROR_003);
        }

        try {
            UserRegistrationCmd registrationCmd = EvoModelMapperUtils.toObject(model, UserRegistrationCmd.class);
            registrationCmd.setUserPass(passwordEncoder.encode(registrationCmd.getUserPass()));
            UserDetailCmd userDetailCmd = EvoModelMapperUtils.toObject(model, UserDetailCmd.class);
            registrationCmd.setUserDetailCmd(userDetailCmd);
            Role role = roleEntityRepository.findByNameAndIsActive(EDefaultRole.USER.value, EActive.ACTIVE.value)
                    .map(roleEntityMapper::toDomain)
                    .orElseThrow(() -> new AppException(ErrConstants.ROLE_DETAIL_ERROR_001));
            registrationCmd.setUserRoleCmds(buildUserRoleCmds(role.getId()));
//            keycloakUtils.registrationWithKeycloak(registrationCmd);
            registrationCmd.setUserPass(passwordEncoder.encode(model.getUserPass()));
            User user = new User(registrationCmd);

            if (userDetailEntityRepository.existsByEmailVerifiedAndUserIdNot(userDetailCmd.getEmail(), user.getId())) {
                throw new AppException(ErrConstants.USER_DETAIL_ERROR_003);
            }

            userDomainRepository.save(user);

            Map<String, UUID> res = new HashMap<>();
            res.put("userId", user.getId());

            return res;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrConstants.SYSTEM_ERROR_001);
        }
    }

    @Override
    public Map<String, Long> enableTfa() {
        String userName = getCurrentUserName();
        validationService.validateCurrentUser();
        validationService.validateAccessToken(userName);

        User user = findUser(userName);
        user.enrichUserDetail(fetchExistingUserDetail(user.getId()));
        if (Objects.equals(user.getIsTfaEnabled(), ETfaStatus.ENABLED.value)) {
            throw new AppException(ErrConstants.TFA_ERROR_001);
        }

        UserCmd userCmd = EvoModelMapperUtils.toObject(user, UserCmd.class);
        userCmd.setIsTfaEnabled(ETfaStatus.ENABLED.value);
        userCmd.setSecretKey(tfaService.generateSecretKey());
        user.enableTfa(userCmd);
        userDomainRepository.save(user);

        Map<String, Long> res = new HashMap<>();
        res.put("status", EResponseStatus.SUCCESS.value);

        return res;
    }

    @Override
    public Map<String, Long> disableTfa() {
        String userName = getCurrentUserName();
        validationService.validateCurrentUser();
        validationService.validateAccessToken(userName);

        User user = findUser(userName);
        user.enrichUserDetail(fetchExistingUserDetail(user.getId()));
        if (Objects.equals(user.getIsTfaEnabled(), ETfaStatus.ENABLED.value)) {
            throw new AppException(ErrConstants.TFA_ERROR_001);
        }

        UserCmd userCmd = EvoModelMapperUtils.toObject(user, UserCmd.class);
        userCmd.setIsTfaEnabled(ETfaStatus.DISABLED.value);
        userCmd.setSecretKey(null);
        user.enableTfa(userCmd);
        userDomainRepository.save(user);

        Map<String, Long> res = new HashMap<>();
        res.put("status", EResponseStatus.SUCCESS.value);

        return res;
    }

    @Override
    public AuthenticationResModel verifyTfa(int tfaCode) {
        String userName = getCurrentUserName();
        validationService.validateTfaToken(userName);

        User user = findUser(userName);

        if (!tfaService.verifyCode(user.getSecretKey(), tfaCode)) {
            throw new AppException(ErrConstants.AUTH_ERROR_005);
        }

        try {
            String accessToken = jwtUtils.generateAccessToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(user);
            Long accessTokenExpireAt = jwtUtils.getExpirationTime(accessToken);
            Long refreshTokenExpireAt = jwtUtils.getExpirationTime(refreshToken);


            TokenInfoCmd cmd = new TokenInfoCmd();
            cmd.setUserId(user.getId());
            cmd.setAccessToken(jwtUtils.getTokenJti(accessToken));
            cmd.setRefreshToken(jwtUtils.getTokenJti(refreshToken));
            cmd.setAccessTokenExpireAt(accessTokenExpireAt);
            cmd.setRefreshTokenExpireAt(refreshTokenExpireAt);
            cmd.setType(ETokenType.ACCESS.value);
            user.saveTokenInfo(cmd);

            userDomainRepository.save(user);

            AuthenticationResModel res = new AuthenticationResModel();
            res.setAccessToken(accessToken);
            res.setRefreshToken(refreshToken);
            res.setAccessTokenExpireAt(accessTokenExpireAt);
            res.setRefreshTokenExpireAt(refreshTokenExpireAt);
            res.setTokenType(ETokenType.ACCESS.value);
            res.setIsTfaEnabled(user.getIsTfaEnabled());
            res.setSecretKey(user.getSecretKey());

            return res;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrConstants.SYSTEM_ERROR_001);
        }
    }

    @Override
    public Map<String, String> sendVerificationEmail() {
        String userName = getCurrentUserName();
        validationService.validateAccessToken(userName);
        User user = findUser(userName);

        user.enrichUserDetail(fetchExistingUserDetail(user.getId()));

        if (Objects.equals(EActive.INACTIVE.value, user.getIsActive())) {
            throw new AppException(ErrConstants.AUTH_ERROR_001);
        }

        UserDetail userDetail = userDetailEntityRepository.findByUserId(user.getId())
                .map(userDetailEntityMapper::toDomain)
                .orElseThrow(() -> new AppException(ErrConstants.USER_DETAIL_ERROR_001));

        if (StringUtils.isEmpty(userDetail.getEmailVerified())
                && StringUtils.isEmpty(userDetail.getEmail())) {
            throw new AppException(ErrConstants.USER_ERROR_003);
        }

        if (!StringUtils.isEmpty(userDetail.getEmailVerified())
                && StringUtils.isEmpty(userDetail.getEmail())) {
            throw new AppException(ErrConstants.USER_ERROR_004);
        }

        Date expireAt = userDetail.getExpireAt();
        boolean isExpired = expireAt == null || expireAt.before(new Date());

        Date newExpireAt = isExpired ? EvoDateUtils.addDay(1) : userDetail.getExpireAt();
        String rawData = userDetail.getUserId() + "|" + newExpireAt.getTime();
        String verifyKey = isExpired ? EvoEncryptionUtils.encrypt(rawData, applicationProperties.getEncryptionKey()) : userDetail.getLinkVerify();

        UserCmd userCmd = EvoModelMapperUtils.toObject(user, UserCmd.class);
        UserDetailCmd userDetailCmd = EvoModelMapperUtils.toObject(userDetail, UserDetailCmd.class);
        userDetailCmd.setLinkVerify(verifyKey);
        userDetailCmd.setExpireAt(newExpireAt);
        userCmd.setUserDetailCmd(userDetailCmd);
        user.saveVerificationLink(userCmd);
        userDomainRepository.save(user);

        Map<String, String> res = new HashMap<>();
        res.put("status", String.valueOf(EResponseStatus.SUCCESS.value));
        res.put("key", verifyKey);

        return res;
    }

    @Override
    public Map<String, Long> verifyEmail(String verifyKey) {
        String userName = getCurrentUserName();
        validationService.validateAccessToken(userName);

        if (StringUtils.isEmpty(verifyKey)) {
            throw new AppException(ErrConstants.INPUT_ERROR_001);
        }

        User user = findUser(userName);
        UserDetail userDetail = userDetailEntityRepository.findByUserId(user.getId())
                .map(userDetailEntityMapper::toDomain)
                .orElseThrow(() -> new AppException(ErrConstants.USER_DETAIL_ERROR_001));

        String decryptedVerifyKey = EvoEncryptionUtils.decrypt(verifyKey, applicationProperties.getEncryptionKey());
        String[] keyParts = decryptedVerifyKey.split("\\|");
        UUID userIdFromKey = UUID.fromString(keyParts[0]);
        if (!Objects.equals(user.getId(), userIdFromKey)) {
            throw new AppException(ErrConstants.USER_ERROR_005);
        }

        if (userDetail.getExpireAt() != null && userDetail.getExpireAt().before(new Date())) {
            throw new AppException(ErrConstants.USER_ERROR_006);
        }

        UserCmd userCmd = EvoModelMapperUtils.toObject(user, UserCmd.class);
        UserDetailCmd userDetailCmd = EvoModelMapperUtils.toObject(userDetail, UserDetailCmd.class);
        userDetailCmd.setEmailVerified(userDetail.getEmail());
        userDetailCmd.setEmail(null);
        userDetailCmd.setLinkVerify(null);
        userDetailCmd.setExpireAt(null);
        userDetailCmd.setIsVerified(EVerify.VERIFIED.value);
        userCmd.setUserDetailCmd(userDetailCmd);
        user.verifiedUser(userCmd);
        userDomainRepository.save(user);

        Map<String, Long> res = new HashMap<>();
        res.put("status", EResponseStatus.SUCCESS.value);

        return res;
    }

    @Override
    public Map<String, Long> signOut() {
        String userName = getCurrentUserName();
        User user = findUser(userName);
        List<TokenInfo> tokenInfo = tokenInfoEntityRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(tokenInfoEntityMapper::toDomain)
                .toList();
        TokenInfo currentUserToken = tokenInfo.getFirst();
        blacklistedTokenService.blacklistedAccessToken(currentUserToken.getAccessToken(), currentUserToken.getAccessTokenExpireAt());
        blacklistedTokenService.blacklistedRefreshToken(currentUserToken.getRefreshToken(), currentUserToken.getRefreshTokenExpireAt());

        Map<String, Long> res = new HashMap<>();
        res.put("status", EResponseStatus.SUCCESS.value);

        return res;
    }

    @Override
    public AuthenticationResModel refreshToken(String refreshToken) {
        return null;
    }

    private String getCurrentUserName() {
        return EvoSecurityUtils.getCurrentUserName();
    }

    private User findUser(String userName) {
        User user = userEntityRepository.findByUserNameAndIsActive(userName, EActive.ACTIVE.value)
                .map(userEntityMapper::toDomain)
                .orElseThrow(() -> new AppException(ErrConstants.USER_DETAIL_ERROR_001));
        user.enrichUserDetail(fetchExistingUserDetail(user.getId()));

        return user;
    }

    private UserDetail fetchExistingUserDetail(UUID userId) {
        return userDetailEntityRepository.findByUserId(userId)
                .map(userDetailEntityMapper::toDomain)
                .orElseThrow(() -> new AppException(ErrConstants.USER_DETAIL_ERROR_001));
    }

    private List<UserRoleCmd> buildUserRoleCmds(UUID roleId) {
        List<UserRoleCmd> userRoleCmds = new ArrayList<>();
        UserRoleCmd userRoleCmd = new UserRoleCmd();
        userRoleCmd.setRoleId(roleId);
        userRoleCmds.add(userRoleCmd);
        return userRoleCmds;
    }
}
