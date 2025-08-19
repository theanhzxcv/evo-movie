package com.evo.identity.infrastructure.persistence.repository.query;

import com.evo.identity.domain.query.RoleQuery;
import com.evo.identity.domain.query.UserQuery;
import com.evo.util.EvoStringUtils;
import jakarta.persistence.Query;

import java.util.UUID;

public class UserRepositoryQuery {

    protected String sqlSearchUser(UserQuery query, Boolean isCount) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT");
        if (isCount) {
            queryBuilder.append(" COUNT(1) ");
        } else {
            queryBuilder.append(" u");
        }

        queryBuilder.append(" FROM UserEntity u");
        queryBuilder.append(" WHERE u.isActive = 1");

        if (!EvoStringUtils.isEmpty(query.getKeyword())) {
            queryBuilder.append(" and (");
            queryBuilder.append(" LOWER(u.userName) LIKE :keyword");
            queryBuilder.append(" or LOWER(u.createdBy) LIKE :keyword");
            queryBuilder.append(" )");
        }

        if (!isCount) {
            queryBuilder.append(" ORDER BY u.updatedAt DESC");
        }

        return queryBuilder.toString();
    }

    protected void queryKeywordParam(Query query, UserQuery userQuery) {
        if (!EvoStringUtils.isEmpty(userQuery.getKeyword())) {
            query.setParameter("keyword", EvoStringUtils.sqlStringSearch(userQuery.getKeyword(), true));
        }
    }

    protected String sqlSearchAssignRole(UUID roleId) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT r.id, r.name, r.description, r.isRoot, r.isDefault, r.type, ");
        queryBuilder.append("CASE WHEN ur.roleId IS NULL THEN 0L ELSE 1L END AS assigned ");
        queryBuilder.append("FROM RoleEntity r ");
        queryBuilder.append("LEFT JOIN UserRoleEntity ur ON ur.roleId = r.id ");

        if (roleId != null) {
            queryBuilder.append("AND ur.userId = :userId ");
        }
        queryBuilder.append("WHERE r.isActive = 1");

        return queryBuilder.toString();
    }

    protected void queryUserIdParam(Query query, UUID userId) {
        if (userId != null) {
            query.setParameter("userId", userId);
        }
    }

    protected String sqlSearchUsersByRoleId(UUID roleId, Boolean isCount) {
        StringBuilder queryBuilder = new StringBuilder();
        if (isCount) {
            queryBuilder.append("SELECT COUNT(DISTINCT u) ");
        } else {
            queryBuilder.append("SELECT DISTINCT u ");
        }
        queryBuilder.append("FROM UserEntity u ");
        queryBuilder.append("JOIN UserRoleEntity ur ON u.id = ur.userId ");
        queryBuilder.append("WHERE u.isActive = 1 ");
        if (roleId != null) {
            queryBuilder.append("AND ur.roleId = :roleId");
        }

        return queryBuilder.toString();
    }

    protected void queryRoleIdParam(Query query, UUID roleId) {
        if (roleId != null) {
            query.setParameter("roleId", roleId);
        }
    }
}
