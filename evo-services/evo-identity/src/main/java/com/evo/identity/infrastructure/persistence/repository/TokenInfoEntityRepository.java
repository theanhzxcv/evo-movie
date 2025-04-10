package com.evo.identity.infrastructure.persistence.repository;

import com.evo.identity.domain.TokenInfo;
import com.evo.identity.infrastructure.persistence.entities.TokenInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TokenInfoEntityRepository extends JpaRepository<TokenInfoEntity, UUID> {
}
