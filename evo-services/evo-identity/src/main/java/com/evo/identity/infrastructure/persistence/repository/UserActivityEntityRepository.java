package com.evo.identity.infrastructure.persistence.repository;

import com.evo.identity.infrastructure.persistence.entities.UserActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserActivityEntityRepository extends JpaRepository<UserActivityEntity, UUID> {

    List<UserActivityEntity> findByUserNameOrderByCreatedAtDesc(String userName);

    List<UserActivityEntity> findByUserNameAndActivityOrderByCreatedAtDesc(String userName, String activity);
}
