package com.evo.identity.infrastructure.persistence.repository;


import com.evo.identity.infrastructure.persistence.entities.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleEntityRepository extends JpaRepository<UserRoleEntity, Long> {
    List<UserRoleEntity> findByUserIdIn(List<UUID> userIds);

    List<UserRoleEntity> findAllByUserIdAndIsActive(UUID userId, Long isActive);
//
//    List<UserRoleEntity> findRoleByUserId(UUID id);
}
