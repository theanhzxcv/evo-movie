//package com.evo.client;
//
//import com.evo.UserAuthority;
//import com.evo.config.FeignClientConfiguration;
//import com.evo.response.Response;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.UUID;
//
//@FeignClient(
//        url = "http://localhost:8081",
//        name = "iam",
//        contextId = "common-iam",
//        configuration = FeignClientConfiguration.class,
//        fallbackFactory = IamClientFallback.class)
//public interface IamClient {
//    @GetMapping("/api/users/{userId}/authorities")
//    Response<UserAuthority> getUserAuthority(@PathVariable UUID userId);
//
//    @GetMapping("/api/users/{username}/authorities-by-username")
//    Response<UserAuthority> getUserAuthority(@PathVariable String username);
//}
