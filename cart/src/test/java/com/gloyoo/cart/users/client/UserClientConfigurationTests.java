package com.gloyoo.cart.users.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

class UserClientConfigurationTests {

    private final RequestInterceptor interceptor =
            new UserClientConfiguration().accessTokenCookieForwarder();

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void forwardsAuthenticationCookiesFromFrontendRequest() {
        MockHttpServletRequest frontendRequest = new MockHttpServletRequest();
        frontendRequest.setCookies(
                new Cookie("accessToken", "access-token"),
                new Cookie("refreshToken", "refresh-token"),
                new Cookie("preferences", "dark-mode")
        );
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(frontendRequest));
        RequestTemplate userServiceRequest = new RequestTemplate();

        interceptor.apply(userServiceRequest);

        assertThat(userServiceRequest.headers().get(HttpHeaders.COOKIE))
                .containsExactly("accessToken=access-token; refreshToken=refresh-token");
    }

    @Test
    void doesNothingOutsideAFrontendRequest() {
        RequestTemplate userServiceRequest = new RequestTemplate();

        interceptor.apply(userServiceRequest);

        assertThat(userServiceRequest.headers()).doesNotContainKey(HttpHeaders.COOKIE);
    }
}
