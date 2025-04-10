package com.evo.identity.domain;

import com.evo.configuration.AuditableDomain;
import com.evo.identity.application.enums.ETokenType;
import com.evo.identity.domain.command.TokenInfoCmd;
import com.evo.util.EvoIdUtils;
import lombok.*;

import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo extends AuditableDomain {
    private UUID id;
    private UUID userId;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireAt;
    private Long refreshTokenExpireAt;
    String type;

    public TokenInfo(TokenInfoCmd cmd) {
        this.id = EvoIdUtils.nextId();
        this.userId = cmd.getUserId();
        this.accessToken = cmd.getAccessToken();
        this.refreshToken = cmd.getRefreshToken();
        this.accessTokenExpireAt = cmd.getAccessTokenExpireAt();
        this.refreshTokenExpireAt = cmd.getRefreshTokenExpireAt();
        this.type = ETokenType.BEARER.value;
    }
}
