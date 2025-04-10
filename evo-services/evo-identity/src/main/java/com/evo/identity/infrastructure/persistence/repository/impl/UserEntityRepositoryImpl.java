package com.evo.identity.infrastructure.persistence.repository.impl;

import com.evo.identity.infrastructure.persistence.entities.RoleEntity;
import com.evo.identity.infrastructure.persistence.entities.UserEntity;
import com.evo.identity.infrastructure.persistence.repository.UserEntityRepositoryCustom;
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

//    @PersistenceContext
//    private EntityManager entityManager;
//    private final UserEntityMapperImpl userEntityMapper;
//
//    // return entity
//    @Override
//    public List<UserEntity> search(UserSearchQuery userSearchQuery) {
//        Map<String, Object> values = new HashMap<>();
//        String sql = "SELECT u FROM UserEntity u "
//                + createWhereQuery(userSearchQuery, values);
//        Query query = entityManager.createQuery(sql, UserEntity.class);
//
//        values.forEach(query::setParameter);
//
//        query.setFirstResult((userSearchQuery.getPageIndex() - 1) * userSearchQuery.getPageSize());
//        query.setMaxResults(userSearchQuery.getPageSize());
//
//        return query.getResultList();
//    }
//
//    @Override
//    public Long count(UserSearchQuery userSearchQuery) {
//        Map<String, Object> values = new HashMap<>();
//        String sql = "select count(u) from UserEntity u " +
//                createWhereQuery(userSearchQuery, values);
//        Query query = entityManager.createQuery(sql, Long.class);
//
//        values.forEach(query::setParameter);
//
//        return (Long) query.getSingleResult();
//    }
//
//    private String createWhereQuery(UserSearchQuery userSearchQuery, Map<String, Object> values) {
//        StringBuilder sql = new StringBuilder();
//        sql.append("where u.deleted = false");
//
//        if (userSearchQuery.getKeyword() != null && !userSearchQuery.getKeyword().trim().isEmpty()) {
//            String keyword = "%" + userSearchQuery.getKeyword().trim().toLowerCase() + "%";
//
//            sql.append(
//                    " AND (LOWER(u.email) LIKE :keyword "
//                            + " OR LOWER(u.username) LIKE :keyword "
//                            + " OR LOWER(u.firstName) LIKE :keyword "
//                            + " OR LOWER(u.lastName) LIKE :keyword "
//                            + " OR LOWER(u.phone) LIKE :keyword) "
//            );
//            values.put("keyword", keyword);
//        }
//        return sql.toString();
//    }
}
