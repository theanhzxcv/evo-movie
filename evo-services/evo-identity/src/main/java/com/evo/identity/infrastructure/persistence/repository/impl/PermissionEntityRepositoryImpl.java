package com.evo.identity.infrastructure.persistence.repository.impl;

import com.evo.identity.domain.query.PermissionQuery;
import com.evo.identity.infrastructure.persistence.entities.PermissionEntity;
import com.evo.identity.infrastructure.persistence.repository.PermissionEntityRepositoryCustom;
import com.evo.identity.infrastructure.persistence.repository.query.PermissionRepositoryQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PermissionEntityRepositoryImpl extends PermissionRepositoryQuery implements PermissionEntityRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PermissionEntity> searchPermissions(PermissionQuery permissionQuery) {
        String jpql = sqlSearchPermission(permissionQuery, false);
        TypedQuery<PermissionEntity> query = entityManager.createQuery(jpql, PermissionEntity.class);
        queryParam(query, permissionQuery);
        query.setFirstResult(permissionQuery.getPageSize() * permissionQuery.getPageIndex());
        query.setMaxResults(permissionQuery.getPageSize());
        return query.getResultList();
    }

    @Override
    public Long countPermissions(PermissionQuery permissionQuery) {
        String jpql = sqlSearchPermission(permissionQuery, true);
        Query query = entityManager.createQuery(jpql);
        queryParam(query, permissionQuery);
        return (Long) query.getSingleResult();
    }
}
