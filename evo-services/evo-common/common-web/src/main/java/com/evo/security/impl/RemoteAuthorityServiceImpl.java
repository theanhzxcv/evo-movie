//package com.evo.security.impl;
//
//import com.evo.UserAuthority;
//import com.evo.client.IamClient;
//import com.evo.dtos.responses.Response;
//import com.evo.security.AuthorityService;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//@Service
//public class RemoteAuthorityServiceImpl implements AuthorityService {
//    private final IamClient iamClient;
//
//    public RemoteAuthorityServiceImpl(IamClient iamClient) {
//        this.iamClient = iamClient;
//    }
//
//    @Override
//    public UserAuthority getUserAuthority(UUID userId) {
//        Response<UserAuthority> response = iamClient.getUserAuthority(userId);
//        return response != null && response.isSuccess() ? response.getData() : null;
//    }
//
//    @Override
//    public UserAuthority getUserAuthority(String username) {
//        Response<UserAuthority> response = iamClient.getUserAuthority(username);
//        return response != null && response.isSuccess() ? response.getData() : null;
//    }
//
//    @Override
//    public UserAuthority getClientAuthority(UUID clientId) {
//        return null;
//    }
//}
