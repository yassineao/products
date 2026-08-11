package com.gloyoo.products;

import com.gloyoo.products.users.client.UserClient;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ActiveProfiles("test")
@SpringBootTest
class ProductsApplicationTests {
    @Autowired
    private WebApplicationContext applicationContext;

    @MockitoBean
    private UserClient userClient;

    private MockMvc mockMvc;

    @BeforeEach
    void configureMockMvc() {
        mockMvc = webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void categoriesArePublic() throws Exception {
        mockMvc.perform(get("/category"))
                .andExpect(status().isOk());

        verifyNoInteractions(userClient);
    }

    @Test
    void staleAccessTokenDoesNotBlockPublicCategories() throws Exception {
        mockMvc.perform(get("/category")
                        .cookie(new Cookie("accessToken", "expired-or-invalid")))
                .andExpect(status().isOk());

        verifyNoInteractions(userClient);
    }

    @Test
    void categoryWritesRequireAuthentication() throws Exception {
        mockMvc.perform(post("/category")
                        .contentType("application/json")
                        .content("""
                                {"name":"Books","description":"Published books","active":true}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void categoryWritesRequireAdminRole() throws Exception {
        when(userClient.me("accessToken=user-service-token")).thenReturn(user("USER"));

        mockMvc.perform(post("/category")
                        .cookie(new Cookie("accessToken", "user-service-token"))
                        .contentType("application/json")
                        .content("""
                                {"name":"Books","description":"Published books","active":true}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminFromUserServiceCanCreateCategory() throws Exception {
        when(userClient.me("accessToken=user-service-token")).thenReturn(user("ADMIN"));

        mockMvc.perform(post("/category")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer user-service-token")
                        .contentType("application/json")
                        .content("""
                                {"name":"Books","description":"Published books","active":true}
                                """))
                .andExpect(status().isOk());
    }

    private Map<String, Object> user(String role) {
        return Map.of(
                "id", UUID.randomUUID().toString(),
                "email", "security-test@example.com",
                "name", "Security Test",
                "role", role
        );
    }

}
