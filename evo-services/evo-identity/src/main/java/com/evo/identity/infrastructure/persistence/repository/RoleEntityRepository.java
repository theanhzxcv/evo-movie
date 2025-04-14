package com.evo.identity.infrastructure.persistence.repository;

import com.evo.identity.infrastructure.persistence.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleEntityRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByNameAndIsActive(String name, Long isActive);

    List<RoleEntity> findAllByNameIsInAndIsActive(List<String> names, Long isActive);

    List<RoleEntity> findAllByIdIsInAndIsActive(List<UUID> roleIds, Long value);
}
