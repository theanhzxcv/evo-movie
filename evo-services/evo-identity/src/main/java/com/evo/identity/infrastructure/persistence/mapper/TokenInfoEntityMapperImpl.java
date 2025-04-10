package com.evo.identity.infrastructure.persistence.mapper;

import com.evo.identity.domain.TokenInfo;
import com.evo.identity.infrastructure.persistence.entities.TokenInfoEntity;
import com.evo.support.DomainEntityMapper;
import com.evo.util.EvoModelMapperUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenInfoEntityMapperImpl implements DomainEntityMapper<TokenInfo, TokenInfoEntity> {
    @Override
    public TokenInfo toDomain(TokenInfoEntity entity) {
        return EvoModelMapperUtils.toObject(entity, TokenInfo.class);
    }

    @Override
    public List<TokenInfo> toDomainList(List<TokenInfoEntity> entities) {
        return List.of();
    }

    @Override
    public TokenInfoEntity toEntity(TokenInfo domain) {
        return EvoModelMapperUtils.toObject(domain, TokenInfoEntity.class);
    }

    @Override
    public List<TokenInfoEntity> toEntityList(List<TokenInfo> domains) {
        return List.of();
    }
}
