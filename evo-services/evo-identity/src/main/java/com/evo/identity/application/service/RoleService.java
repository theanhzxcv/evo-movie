package com.evo.identity.application.service;

import com.evo.identity.application.model.AssignPermissionResModel;
import com.evo.identity.application.model.PermissionSearchReqModel;
import com.evo.identity.application.model.RoleDeleteReqModel;
import com.evo.identity.application.model.RoleDetailResModel;
import com.evo.identity.application.model.RoleReqModel;
import com.evo.identity.application.model.RoleResModel;
import com.evo.identity.application.model.RoleSearchReqModel;
import com.evo.identity.application.model.RoleSearchResModel;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RoleService {

    Map<String, Long> create(RoleReqModel model);

    RoleResModel update(UUID id, RoleReqModel model);

    RoleResModel delete(RoleDeleteReqModel model);

    RoleResModel restore(UUID id);

    Page<RoleSearchResModel> search(RoleSearchReqModel model);

    RoleDetailResModel details(UUID id);

    List<AssignPermissionResModel> getAssignPermissions(UUID id);
}
