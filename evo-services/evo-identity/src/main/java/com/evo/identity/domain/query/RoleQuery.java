package com.evo.identity.domain.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleQuery {
    private String keyword;
    private int pageIndex;
    private int pageSize;
}
