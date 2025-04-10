package com.evo.identity.application.service.impl;

import com.evo.constans.ErrConstans;
import com.evo.exception.AppException;
import com.evo.identity.application.enums.EActive;
import com.evo.identity.application.model.AuthenticationReqModel;
import com.evo.identity.application.model.AuthenticationResModel;
import com.evo.identity.application.model.RegistrationReqModel;
import com.evo.identity.application.security.JwtUtil;
import com.evo.identity.application.service.AuthenticationService;
import com.evo.identity.domain.User;
import com.evo.identity.domain.repository.UserDomainRepository;
import com.evo.identity.infrastructure.persistence.entities.UserEntity;
import com.evo.identity.infrastructure.persistence.repository.UserDetailEntityRepository;
import com.evo.identity.infrastructure.persistence.repository.UserEntityRepository;
import com.evo.util.EvoModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakAuthenticationServiceImpl implements AuthenticationService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepository userEntityRepository;
    private final UserDomainRepository userDomainRepository;
    private final UserDetailEntityRepository userDetailEntityRepository;

    @Override
    public AuthenticationResModel signIn(AuthenticationReqModel model) {
        return null;
    }

    @Override
    public Map<String, Long> signUp(RegistrationReqModel model) {
        return Map.of();
    }

    @Override
    public AuthenticationResModel tfaRequired() {
        return null;
    }

    private User findUser(String userName) {
        UserEntity userEntity = userEntityRepository.findByUserNameAndIsActive(userName, EActive.ACTIVE.value)
                .orElseThrow(() -> new AppException(ErrConstans.USER_DETAIL_ERROR_001));

        return EvoModelMapperUtils.toObject(userEntity, User.class);
    }
}
