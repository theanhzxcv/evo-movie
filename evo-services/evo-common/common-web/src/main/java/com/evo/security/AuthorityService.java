package com.evo.security;

import com.evo.UserAuthority;

import java.util.UUID;

public interface AuthorityService {
    UserAuthority getUserAuthority(UUID userId);

    UserAuthority getUserAuthority(String email);

    UserAuthority getClientAuthority(UUID clientId);
}
