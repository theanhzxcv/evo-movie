package com.evo.identity.domain;

import com.evo.configuration.AuditableDomain;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserRole extends AuditableDomain {
    private UUID id;
    private UUID userId;
    private UUID roleId;
    private Long isActive;
}
