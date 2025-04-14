package com.evo.identity.infrastructure.persistence.repository;

import com.evo.identity.infrastructure.persistence.entities.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionEntityRepository extends JpaRepository<PermissionEntity, UUID> {
    Optional<PermissionEntity> findByNameAndIsActive(String name, Long isActive);

    List<PermissionEntity> findAllByNameIsInAndIsActive(List<String> names, Long isActive);

    List<PermissionEntity> findAllByIdIsInAndIsActive(List<UUID> ids, Long isActive);
}
