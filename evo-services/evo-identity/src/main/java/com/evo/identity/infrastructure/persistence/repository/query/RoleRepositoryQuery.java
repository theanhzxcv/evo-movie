package com.evo.identity.infrastructure.persistence.repository.query;

import com.evo.identity.domain.query.RoleQuery;
import com.evo.util.EvoStringUtils;
import jakarta.persistence.Query;

import java.util.UUID;

public class RoleRepositoryQuery {

    protected String sqlSearchPermission(RoleQuery query, Boolean isCount) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT");
        if (isCount) {
            queryBuilder.append(" COUNT(1) ");
        } else {
            queryBuilder.append(" r");
        }

        queryBuilder.append(" FROM RoleEntity r");
        queryBuilder.append(" WHERE r.isActive = 1");

        if (!EvoStringUtils.isEmpty(query.getKeyword())) {
            queryBuilder.append(" and (");
            queryBuilder.append(" LOWER(r.name) LIKE :keyword");
            queryBuilder.append(" or LOWER(r.createdBy) LIKE :keyword");
            queryBuilder.append(" )");
        }

        if (!isCount) {
            queryBuilder.append(" ORDER BY r.updatedAt DESC");
        }

        return queryBuilder.toString();
    }

    protected void queryKeywordParam(Query query, RoleQuery roleQuery) {
        if (!EvoStringUtils.isEmpty(roleQuery.getKeyword())) {
            query.setParameter("keyword", EvoStringUtils.sqlStringSearch(roleQuery.getKeyword(), true));
        }
    }

    protected String sqlSearchAssignPermission(UUID roleId) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT p.id, p.name, p.description, p.resource, p.scope, ");
        queryBuilder.append("CASE WHEN rp.permissionId IS NULL THEN 0L ELSE 1L END AS assigned ");
        queryBuilder.append("FROM PermissionEntity p ");
        queryBuilder.append("LEFT JOIN RolePermissionEntity rp ON rp.permissionId = p.id ");

        if (roleId != null) {
            queryBuilder.append("AND rp.roleId = :roleId ");
        }
        queryBuilder.append("WHERE p.isActive = 1");

        return queryBuilder.toString();
    }

    protected void queryRoleIdParam(Query query, UUID roleId) {
        if (roleId != null) {
            query.setParameter("roleId", roleId);
        }
    }
}
