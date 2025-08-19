package com.evo.identity.infrastructure.persistence.repository.impl;

import com.evo.identity.application.model.AssignPermissionResModel;
import com.evo.identity.application.model.AssignRoleResModel;
import com.evo.identity.domain.query.UserQuery;
import com.evo.identity.infrastructure.persistence.entities.UserEntity;
import com.evo.identity.infrastructure.persistence.repository.custom.UserEntityRepositoryCustom;
import com.evo.identity.infrastructure.persistence.repository.query.UserRepositoryQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserEntityRepositoryImpl extends UserRepositoryQuery implements UserEntityRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<UserEntity> searchUsers(UserQuery userQuery) {
        String jpql = sqlSearchUser(userQuery, false);
        TypedQuery<UserEntity> query = entityManager.createQuery(jpql, UserEntity.class);
        queryKeywordParam(query, userQuery);
        query.setFirstResult(userQuery.getPageSize() * userQuery.getPageIndex());
        query.setMaxResults(userQuery.getPageSize());
        return query.getResultList();
    }

    @Override
    public Long countUsers(UserQuery userQuery) {
        String jpql = sqlSearchUser(userQuery, true);
        Query query = entityManager.createQuery(jpql);
        queryKeywordParam(query, userQuery);
        return (Long) query.getSingleResult();
    }

    @Override
    public List<AssignRoleResModel> getAssignRolesByUserId(UUID userId) {
        String jpql = sqlSearchAssignRole(userId);
        TypedQuery<AssignRoleResModel> query = entityManager.createQuery(jpql, AssignRoleResModel.class);
        queryUserIdParam(query, userId);

        return query.getResultList();
    }

    @Override
    public List<UserEntity> searchUsersByRoleId(UUID roleId) {
        String jpql = sqlSearchUsersByRoleId(roleId, false);
        TypedQuery<UserEntity> query = entityManager.createQuery(jpql, UserEntity.class);
        queryRoleIdParam(query, roleId);

        return query.getResultList();
    }

    @Override
    public Long countUsersByRoleId(UUID roleId) {
        String jpql = sqlSearchUsersByRoleId(roleId, true);
        Query query = entityManager.createQuery(jpql);
        queryRoleIdParam(query, roleId);

        return (Long) query.getSingleResult();
    }
}
