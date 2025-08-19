package com.evo.identity.application.service.impl;

import com.evo.constants.ErrConstants;
import com.evo.exception.AppException;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.application.enums.EActivityType;
import com.evo.identity.application.enums.ELockThreshold;
import com.evo.identity.application.enums.EResponseStatus;
import com.evo.identity.application.model.ChangePasswordReqModel;
import com.evo.identity.application.model.ProfileReqModel;
import com.evo.identity.application.model.ProfileResModel;
import com.evo.identity.application.service.UserService;
import com.evo.identity.domain.TokenInfo;
import com.evo.identity.domain.User;
import com.evo.identity.domain.UserActivity;
import com.evo.identity.domain.UserDetail;
import com.evo.identity.domain.UserRole;
import com.evo.identity.domain.command.UserActivityCmd;
import com.evo.identity.domain.command.UserCmd;
import com.evo.identity.domain.command.UserDetailCmd;
import com.evo.identity.domain.command.UserRoleCmd;
import com.evo.identity.domain.repository.UserDomainRepository;
import com.evo.identity.infrastructure.persistence.mapper.TokenInfoEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserActivityEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserDetailEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserRoleEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.TokenInfoEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserActivityEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserDetailEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserRoleEntityRepository;
import com.evo.security.validation.BlacklistedTokenService;
import com.evo.security.validation.impl.BlacklistedTokenServiceImpl;
import com.evo.util.EvoDateUtils;
import com.evo.util.EvoModelMapperUtils;
import com.evo.util.EvoSecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserEntityRepository userEntityRepository;
    private final UserDomainRepository userDomainRepository;
    private final UserDetailEntityRepository userDetailEntityRepository;
    private final UserRoleEntityRepository userRoleEntityRepository;
    private final UserActivityEntityRepository userActivityEntityRepository;
    private final TokenInfoEntityRepository tokenInfoEntityRepository;
    private final UserEntityMapperImpl userEntityMapper;
    private final UserDetailEntityMapperImpl userDetailEntityMapper;
    private final UserRoleEntityMapperImpl userRoleEntityMapper;
    private final UserActivityEntityMapperImpl userActivityEntityMapper;
    private final TokenInfoEntityMapperImpl tokenInfoEntityMapper;
    private final BlacklistedTokenServiceImpl blacklistedTokenService;
    private final PasswordEncoder passwordEncoder;



    @Override
    public ProfileResModel profile() {
        String userName = getCurrentUserName();
        User user = findUser(userName);
        return EvoModelMapperUtils.toObject(user.getUserDetail(), ProfileResModel.class);
    }

    @Override
    public Map<String, Long> update(ProfileReqModel model) {
        User user = findUser(getCurrentUserName());
        UserDetailCmd userDetailCmd = EvoModelMapperUtils.toObject(user.getUserDetail(), UserDetailCmd.class);
        userDetailCmd.setBio(model.getBio());
        userDetailCmd.setFirstName(model.getFirstName());
        userDetailCmd.setLastName(model.getLastName());
        userDetailCmd.setGender(model.getGender());
        userDetailCmd.setPhoneNumber(model.getPhoneNumber());
        userDetailCmd.setDateOfBirth(model.getDateOfBirth());
        userDetailCmd.setAddressLine(model.getAddressLine());
        userDetailCmd.setProvince(model.getProvince());
        userDetailCmd.setCountry(model.getCountry());
        UserCmd userCmd = EvoModelMapperUtils.toObject(user, UserCmd.class);
        userCmd.setUserDetailCmd(userDetailCmd);
        user.update(userCmd);
        userDomainRepository.save(user);

        Map<String, Long> res = new HashMap<>();
        res.put("status", EResponseStatus.SUCCESS.value);

        return res;
    }

    @Override
    public Map<String, Long> changePassword(ChangePasswordReqModel model) {
        User user = findUser(getCurrentUserName());
        UserCmd userCmd = EvoModelMapperUtils.toObject(user, UserCmd.class);

        if (!passwordEncoder.matches(model.getCurrentPass(), user.getUserPass())) {
            List<UserActivity> userActivities =
                    userActivityEntityRepository
                            .findByUserNameOrderByCreatedAtDesc(user.getUserName())
                            .stream().map(userActivityEntityMapper::toDomain)
                            .toList();

            UserActivity latestChangePwActivity = userActivities.isEmpty() ? null : userActivities.getFirst();
            UserActivityCmd latestChangePwActivityCmd;
            if (latestChangePwActivity != null &&
                    EActivityType.CHANGE_PASSWORD.value.equals(latestChangePwActivity.getActivity()) &&
                    EResponseStatus.FAILED.value.equals(latestChangePwActivity.getStatus())) {
                    latestChangePwActivityCmd = EvoModelMapperUtils.toObject(latestChangePwActivity, UserActivityCmd.class);
                    user.enrichUserActivity(latestChangePwActivity);
            } else {
                latestChangePwActivityCmd = new UserActivityCmd();
                latestChangePwActivityCmd.setUserName(user.getUserName());
                latestChangePwActivityCmd.setRetryCount(ELockThreshold.RESET.value);
                latestChangePwActivityCmd.setLockUntil(null);
                latestChangePwActivityCmd.setStatus(EResponseStatus.FAILED.value);
                latestChangePwActivityCmd.setActivity(EActivityType.CHANGE_PASSWORD.value);
                userCmd.setUserActivityCmd(latestChangePwActivityCmd);
                user.update(userCmd);

                userDomainRepository.save(user);
            }

            long failedAttempts = latestChangePwActivityCmd.getRetryCount() + ELockThreshold.THRESHOLD_MIN.value;
            latestChangePwActivityCmd.setRetryCount(failedAttempts);
            userCmd.setUserActivityCmd(latestChangePwActivityCmd);
            user.update(userCmd);
            userDomainRepository.save(user);

            if (Objects.equals(ELockThreshold.THRESHOLD_LOCK.value, latestChangePwActivityCmd.getRetryCount())) {
                UserActivityCmd changePwFailedActivityCmd = new UserActivityCmd();
                changePwFailedActivityCmd.setUserName(user.getUserName());
                changePwFailedActivityCmd.setActivity(EActivityType.LOGIN.value);
                changePwFailedActivityCmd.setRetryCount(ELockThreshold.THRESHOLD_LOCK.value);
                changePwFailedActivityCmd.setLockUntil(EvoDateUtils.addMinute(new Date(), ELockThreshold.LOCK_DURATION.value.intValue()));
                changePwFailedActivityCmd.setStatus(EResponseStatus.FAILED.value);

                List<TokenInfo> tokenInfo = tokenInfoEntityRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                        .map(tokenInfoEntityMapper::toDomain)
                        .toList();
                TokenInfo currentUserToken = tokenInfo.getFirst();
                blacklistedTokenService.blacklistedAccessToken(currentUserToken.getAccessToken(), currentUserToken.getAccessTokenExpireAt());
                blacklistedTokenService.blacklistedRefreshToken(currentUserToken.getRefreshToken(), currentUserToken.getRefreshTokenExpireAt());

                userCmd.setUserActivityCmd(changePwFailedActivityCmd);
                user.update(userCmd);
                userDomainRepository.save(user);
                throw new AppException(ErrConstants.LOGIN_ERROR_003);
            } else if (Objects.equals(ELockThreshold.THRESHOLD_WAIT_LOCK.value, latestChangePwActivityCmd.getRetryCount())) {
                throw new AppException(ErrConstants.CHANGE_PASSWORD_ERROR_003);
            } else if (Objects.equals(ELockThreshold.THRESHOLD_WARN_HIGH.value, latestChangePwActivityCmd.getRetryCount())
                    || Objects.equals(ELockThreshold.THRESHOLD_WARN_LOW.value, latestChangePwActivityCmd.getRetryCount())) {
                String message = String.format(ErrConstants.CHANGE_PASSWORD_ERROR_002.getErrDesc(), latestChangePwActivityCmd.getRetryCount());
                throw new AppException(ErrConstants.CHANGE_PASSWORD_ERROR_002.getErrCode(), message);
            } else {
                throw new AppException(ErrConstants.CHANGE_PASSWORD_ERROR_004);
            }
        }

        if (model.getNewPass().equals(model.getCurrentPass())) {
            throw new AppException(ErrConstants.CHANGE_PASSWORD_ERROR_001);
        }

        userCmd.setUserPass(passwordEncoder.encode(model.getNewPass()));
        user.changePassword(userCmd);
        userDomainRepository.save(user);

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

    private String getCurrentUserName() {
        return EvoSecurityUtils.getCurrentUserName();
    }

    private User findUser(String userName) {
        User user = userEntityRepository.findByUserNameAndIsActive(userName, EActive.ACTIVE.value)
                .map(userEntityMapper::toDomain)
                .orElseThrow(() -> new AppException(ErrConstants.USER_DETAIL_ERROR_001));
        user.enrichUserDetail(fetchExistingUserDetail(user.getId()));
        user.enrichUserRole(fetchExistingRole(user.getId()));

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
}
