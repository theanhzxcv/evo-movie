package com.evo.identity.application.service;

import com.evo.identity.application.model.ChangePasswordReqModel;
import com.evo.identity.application.model.ProfileReqModel;
import com.evo.identity.application.model.ProfileResModel;

import java.util.Map;

public interface UserService {
    ProfileResModel profile();

    Map<String, Long> update(ProfileReqModel model);

    Map<String, Long> changePassword(ChangePasswordReqModel model);
}
