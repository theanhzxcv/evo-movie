package com.evo.identity.infrastructure.persistence.repository.query;

import jakarta.persistence.Query;

import java.util.UUID;

public class UserRepositoryQuery {

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
