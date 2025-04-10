package com.evo.identity.infrastructure.persistence.repository.impl;

import com.evo.identity.application.model.AssignPermissionResModel;
import com.evo.identity.domain.query.RoleQuery;
import com.evo.identity.infrastructure.persistence.entities.RoleEntity;
import com.evo.identity.infrastructure.persistence.repository.RoleEntityRepositoryCustom;
import com.evo.identity.infrastructure.persistence.repository.query.RoleRepositoryQuery;
import com.evo.util.EvoModelMapperUtils;
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
public class RoleEntityRepositoryImpl extends RoleRepositoryQuery implements RoleEntityRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RoleEntity> searchRoles(RoleQuery roleQuery) {
        String jpql = sqlSearchPermission(roleQuery, false);
        TypedQuery<RoleEntity> query = entityManager.createQuery(jpql, RoleEntity.class);
        queryKeywordParam(query, roleQuery);
        query.setFirstResult(roleQuery.getPageSize() * roleQuery.getPageIndex());
        query.setMaxResults(roleQuery.getPageSize());
        return query.getResultList();
    }

    @Override
    public Long countRoles(RoleQuery roleQuery) {
        String jpql = sqlSearchPermission(roleQuery, true);
        Query query = entityManager.createQuery(jpql);
        queryKeywordParam(query, roleQuery);
        return (Long) query.getSingleResult();
    }

    @Override
    public List<AssignPermissionResModel> getAssignPermissionsByRoleId(UUID roleId) {
        String jpql = sqlSearchAssignPermission(roleId);
        TypedQuery<AssignPermissionResModel> query = entityManager.createQuery(jpql, AssignPermissionResModel.class);
        queryRoleIdParam(query, roleId);

        return query.getResultList();
    }
}
