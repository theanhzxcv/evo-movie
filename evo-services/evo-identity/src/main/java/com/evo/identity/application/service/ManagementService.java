package com.evo.identity.application.service;

import com.evo.identity.application.model.UserDetailResModel;
import com.evo.identity.application.model.UserReqModel;
import com.evo.identity.application.model.UserResModel;
import com.evo.identity.application.model.UserSearchReqModel;
import com.evo.identity.application.model.UserSearchResModel;
import com.evo.identity.domain.query.UserQuery;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.UUID;

public interface ManagementService {

    Map<String, Long> create(UserReqModel model);

    UserResModel update(UUID id, UserReqModel model);

    UserResModel delete(UUID id);

    UserResModel restore(UUID id);

    Page<UserSearchResModel> search(UserSearchReqModel model);

    UserDetailResModel details(UUID id);
}
