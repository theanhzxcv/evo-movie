package com.evo.identity.presentation.controller;

import com.evo.identity.application.model.AssignPermissionResModel;
import com.evo.identity.application.model.PermissionReqModel;
import com.evo.identity.application.model.PermissionResModel;
import com.evo.identity.application.model.PermissionSearchReqModel;
import com.evo.identity.application.model.PermissionSearchResModel;
import com.evo.identity.application.model.RoleDeleteReqModel;
import com.evo.identity.application.model.RoleDetailResModel;
import com.evo.identity.application.model.RoleReqModel;
import com.evo.identity.application.model.RoleResModel;
import com.evo.identity.application.model.RoleSearchReqModel;
import com.evo.identity.application.model.RoleSearchResModel;
import com.evo.identity.application.service.PermissionService;
import com.evo.identity.application.service.RoleService;
import com.evo.response.PageResponse;
import com.evo.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    @PostMapping("/role")
    public Response<Object> createRole(@RequestBody @Valid RoleReqModel roleReqModel) {
        return Response.of(roleService.create(roleReqModel));
    }

    @PutMapping("/role/{id}")
    public Response<RoleResModel> updateRole(
            @PathVariable("id") UUID id,
            @RequestBody @Valid RoleReqModel roleReqModel) {
        return Response.of(roleService.update(id, roleReqModel));
    }

    @DeleteMapping("/role")
    public Response<RoleResModel> deleteRole(@RequestBody @Valid RoleDeleteReqModel model) {
        return Response.of(roleService.delete(model));
    }

    @PutMapping("/role/restore/{id}")
    public Response<RoleResModel> restoreRole(@PathVariable("id") UUID id) {
        return Response.of(roleService.restore(id));
    }

    @GetMapping("/role/search")
    public PageResponse<RoleSearchResModel> searchPermissions(RoleSearchReqModel model) {
        Page<RoleSearchResModel> resModels = roleService.search(model);

        return PageResponse.of(resModels.getContent(),
                resModels.getNumber(),
                resModels.getSize(),
                resModels.getTotalElements());
    }

    @GetMapping("/role/details/{id}")
    public Response<RoleDetailResModel> roleDetails(@PathVariable("id") UUID id) {
        return Response.of(roleService.details(id));
    }

    @GetMapping("/role/assign/{roleId}")
    public Response<List<AssignPermissionResModel>> assignPermission(@PathVariable("roleId") UUID roleId) {
        return Response.of(roleService.getAssignPermissions(roleId));
    }

    @PostMapping("/permission")
    public Response<Object> createPermission(@RequestBody @Valid PermissionReqModel model) {
        return Response.of(permissionService.create(model));
    }

    @PutMapping("/permission/{id}")
    public Response<PermissionResModel> updatePermission(@PathVariable("id") UUID id,
                                                         @Valid @RequestBody PermissionReqModel model) {
        return Response.of(permissionService.update(id, model));
    }

    @DeleteMapping("/permission/{id}")
    public Response<PermissionResModel> deletePermission(@PathVariable("id") UUID id) {
        return Response.of(permissionService.delete(id));
    }

    @PutMapping("/permission/restore/{id}")
    public Response<PermissionResModel> restorePermission(@PathVariable("id") UUID id) {
        return Response.of(permissionService.restore(id));
    }

    @GetMapping("/permission/search")
    public PageResponse<PermissionSearchResModel> searchPermissions(PermissionSearchReqModel model) {
        Page<PermissionSearchResModel> resModels = permissionService.search(model);

        return PageResponse.of(resModels.getContent(),
                resModels.getNumber(),
                resModels.getSize(),
                resModels.getTotalElements());
    }

    @GetMapping("/permission/details/{id}")
    public Response<PermissionResModel> detailPermission(@PathVariable("id") UUID id) {
        return Response.of(permissionService.details(id));
    }
}
