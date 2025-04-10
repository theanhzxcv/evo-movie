package com.evo.identity.infrastructure.persistence.repository;


import com.evo.identity.infrastructure.persistence.entities.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserEntityRepositoryCustom {

//    List<UserEntity> search(UserSearchQuery userSearchQuery);
//
//    Long count(UserSearchQuery userSearchQuery);

    List<UserEntity> searchUsersByRoleId(UUID roleId);

    Long countUsersByRoleId(UUID roleId);
}
