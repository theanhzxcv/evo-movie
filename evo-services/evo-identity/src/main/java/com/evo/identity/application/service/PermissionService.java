package com.evo.identity.application.service;

import com.evo.identity.application.model.PermissionReqModel;
import com.evo.identity.application.model.PermissionResModel;
import com.evo.identity.application.model.PermissionSearchReqModel;
import com.evo.identity.application.model.PermissionSearchResModel;
import org.springframework.data.domain.Page;


import java.util.Map;
import java.util.UUID;

public interface PermissionService {

    Map<String, Long> create(PermissionReqModel model);

    PermissionResModel update(UUID id, PermissionReqModel model);

    PermissionResModel delete(UUID id);

    PermissionResModel restore(UUID id);

    Page<PermissionSearchResModel> search(PermissionSearchReqModel model);

    PermissionResModel details(UUID id);
}
