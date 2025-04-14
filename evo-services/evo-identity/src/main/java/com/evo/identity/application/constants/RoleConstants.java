package com.evo.identity.application.constants;

import com.evo.identity.application.enums.EDefaultRole;
import com.evo.identity.application.enums.ERoleType;
import com.evo.identity.application.enums.ERootRole;

public class RoleConstants {
    public static final String ADMIN_ROLE_NAME = EDefaultRole.ADMIN.value;
    public static final String ADMIN_ROLE_DESCRIPTION = "Administrator role with full access";
    public static final Long ADMIN_ROLE_ROOT = ERootRole.ROOT.value;
    public static final String ADMIN_ROLE_TYPE = ERoleType.ADMIN.value;

    public static final String USER_ROLE_NAME = EDefaultRole.USER.value;
    public static final String USER_ROLE_DESCRIPTION = "Standard user role with limited access";
    public static final Long USER_ROLE_ROOT = ERootRole.NON_ROOT.value;
    public static final String USER_ROLE_TYPE = ERoleType.USER.value;
}
