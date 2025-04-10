package com.evo.identity.infrastructure.persistence.repository;

import com.evo.identity.infrastructure.persistence.entities.UserDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDetailEntityRepository extends JpaRepository<UserDetailEntity, UUID> {
    Optional<UserDetailEntity> findByEmail(String email);
}
