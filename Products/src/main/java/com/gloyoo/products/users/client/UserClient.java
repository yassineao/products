package com.gloyoo.products.users.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(
        name = "user-backend-service",
        url = "${user-backend.url}",
        configuration = UserClientConfiguration.class
    )
public interface UserClient {

    @GetMapping("/user/me")
    Map <String, Object> me(@RequestHeader(HttpHeaders.COOKIE) String cookieHeader);


}
