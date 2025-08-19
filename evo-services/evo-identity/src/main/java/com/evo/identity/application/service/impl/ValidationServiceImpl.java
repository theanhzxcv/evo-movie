package com.evo.identity.application.service.impl;

import com.evo.constants.ErrConstants;
import com.evo.exception.AppException;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.application.enums.ETokenType;
import com.evo.identity.application.enums.EVerify;
import com.evo.identity.domain.TokenInfo;
import com.evo.identity.domain.User;
import com.evo.identity.domain.UserDetail;
import com.evo.identity.infrastructure.persistence.entities.UserEntity;
import com.evo.identity.infrastructure.persistence.mapper.TokenInfoEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserDetailEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.mapper.UserEntityMapperImpl;
import com.evo.identity.infrastructure.persistence.repository.TokenInfoEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserDetailEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserEntityRepository;
import com.evo.security.validation.ValidationService;
import com.evo.util.EvoSecurityUtils;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Primary
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final UserEntityRepository userEntityRepository;
    private final UserDetailEntityRepository userDetailEntityRepository;
    private final TokenInfoEntityRepository tokenInfoEntityRepository;
    private final UserEntityMapperImpl userEntityMapper;
    private final UserDetailEntityMapperImpl userDetailEntityMapper;
    private final TokenInfoEntityMapperImpl tokenInfoEntityMapper;

    @Override
    public void validateCurrentUser() {
        String userName = getCurrentUserName();
        User user = userEntityRepository.findByUserNameAndIsActive(userName, EActive.ACTIVE.value)
                .map(userEntityMapper::toDomain)
                .orElseThrow(() -> new AppException(ErrConstants.AUTH_ERROR_001));

        UserDetail userDetail = userDetailEntityRepository.findByUserId(user.getId())
                .map(userDetailEntityMapper::toDomain)
                .orElseThrow(() -> new AppException(ErrConstants.USER_DETAIL_ERROR_001));
        if (Objects.equals(EVerify.UNVERIFIED.value, userDetail.getIsVerified())) {
            if (!StringUtils.isEmpty(userDetail.getEmail())) {
                throw new AppException(ErrConstants.ACCOUNT_ERROR_001);
            } else {
                throw new AppException(ErrConstants.ACCOUNT_ERROR_002);
            }
        }
    }

    @Override
    public void validateAccessToken(String username) {
        UserEntity userEntity = userEntityRepository.findByUserNameAndIsActive(username, EActive.ACTIVE.value)
                .orElseThrow(() -> new AppException(ErrConstants.SESSION_ERROR_001));
        List<TokenInfo> tokenInfos = tokenInfoEntityRepository.findAllByUserIdOrderByCreatedAtDesc(userEntity.getId())
                .stream().map(tokenInfoEntityMapper::toDomain)
                .toList();
        TokenInfo hisTokenInfo = tokenInfos.getFirst();
        if (!Objects.equals(ETokenType.ACCESS.value, hisTokenInfo.getType())) {
            throw new AppException(ErrConstants.SESSION_ERROR_001);
        }
    }

    @Override
    public void validateTfaToken(String username) {
        UserEntity userEntity = userEntityRepository.findByUserNameAndIsActive(username, EActive.ACTIVE.value)
                .orElseThrow(() -> new AppException(ErrConstants.SESSION_ERROR_001));

        List<TokenInfo> tokenInfos = tokenInfoEntityRepository.findAllByUserIdOrderByCreatedAtDesc(userEntity.getId())
                .stream().map(tokenInfoEntityMapper::toDomain)
                .toList();
        TokenInfo hisTokenInfo = tokenInfos.getFirst();
        if (!Objects.equals(ETokenType.TFA.value, hisTokenInfo.getType())) {
            throw new AppException(ErrConstants.SESSION_ERROR_001);
        }
    }

    @Override
    public void validateResetToken() {

    }

    private String getCurrentUserName() {
        return EvoSecurityUtils.getCurrentUserName();
    }
}
