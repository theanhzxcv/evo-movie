package com.evo.identity.domain.repository;

import com.evo.identity.domain.User;
import com.evo.support.DomainRepository;

import java.util.UUID;

public interface UserDomainRepository extends DomainRepository<User, UUID> {
}
