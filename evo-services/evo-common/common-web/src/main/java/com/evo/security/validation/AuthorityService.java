package com.evo.security.validation;

import com.evo.UserAuthority;

import java.util.UUID;

public interface AuthorityService {
    UserAuthority getUserAuthority(UUID userId);

    UserAuthority getUserAuthority(String username);

    UserAuthority getClientAuthority(UUID clientId);
}
