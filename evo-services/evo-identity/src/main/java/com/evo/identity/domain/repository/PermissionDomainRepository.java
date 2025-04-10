package com.evo.identity.domain.repository;

import com.evo.identity.domain.Permission;
import com.evo.support.DomainRepository;

import java.util.UUID;

public interface PermissionDomainRepository extends DomainRepository<Permission, UUID> {}
