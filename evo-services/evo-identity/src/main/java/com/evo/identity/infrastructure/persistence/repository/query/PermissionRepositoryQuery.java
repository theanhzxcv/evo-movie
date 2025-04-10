package com.evo.identity.infrastructure.persistence.repository.query;

import com.evo.identity.domain.query.PermissionQuery;
import com.evo.util.EvoStringUtils;
import jakarta.persistence.Query;

public class PermissionRepositoryQuery {

    protected String sqlSearchPermission(PermissionQuery query, Boolean isCount) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT");
        if (isCount) {
            queryBuilder.append(" COUNT(1) ");
        } else {
            queryBuilder.append(" p");
        }

        queryBuilder.append(" FROM PermissionEntity p");
        queryBuilder.append(" WHERE p.isActive = 1");

        if (!EvoStringUtils.isEmpty(query.getKeyword())) {
            queryBuilder.append(" and (");
            queryBuilder.append(" LOWER(p.name) LIKE :keyword");
            queryBuilder.append(" or LOWER(p.createdBy) LIKE :keyword");
            queryBuilder.append(" )");
        }

        if (!isCount) {
            queryBuilder.append(" ORDER BY p.updatedAt DESC");
        }

        return queryBuilder.toString();
    }

    protected void queryParam(Query query, PermissionQuery permissionQuery) {
        if (!EvoStringUtils.isEmpty(permissionQuery.getKeyword())) {
            query.setParameter("keyword", EvoStringUtils.sqlStringSearch(permissionQuery.getKeyword(), true));
        }
    }
}
