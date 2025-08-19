package com.evo.identity.presentation.controller;

import com.evo.identity.application.model.RoleSearchResModel;
import com.evo.identity.application.model.UserDetailResModel;
import com.evo.identity.application.model.UserReqModel;
import com.evo.identity.application.model.UserResModel;
import com.evo.identity.application.model.UserSearchReqModel;
import com.evo.identity.application.model.UserSearchResModel;
import com.evo.identity.application.service.ManagementService;
import com.evo.identity.application.service.RoleService;
import com.evo.response.PageResponse;
import com.evo.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
public class ManagementController {

    private final ManagementService managementService;
    private final RoleService roleService;

    @PreAuthorize("hasPermission('User', 'Create')")
    @PostMapping("/user")
    public Response<Object> createUser(@RequestBody @Valid UserReqModel model) {
        return Response.of(managementService.create(model));
    }

    @PreAuthorize("hasPermission('User', 'Update')")
    @PutMapping("/user/{id}")
    public Response<UserResModel> updateUser(@PathVariable("id") UUID id, @RequestBody @Valid UserReqModel model) {
        return Response.of(managementService.update(id, model));
    }

    @PreAuthorize("hasPermission('User', 'Read')")
    @GetMapping("/user/search")
    public PageResponse<UserSearchResModel> searchUser(UserSearchReqModel model) {
        Page<UserSearchResModel> resModels = managementService.search(model);

        return PageResponse.of(resModels.getContent(),
                resModels.getNumber(),
                resModels.getSize(),
                resModels.getTotalElements());
    }

    @PreAuthorize("hasPermission('User', 'Read')")
    @GetMapping("/user/details/{userId}")
    public Response<UserDetailResModel> searchUser(@PathVariable("userId") UUID userId) {
        return Response.of(managementService.details(userId));
    }
}
