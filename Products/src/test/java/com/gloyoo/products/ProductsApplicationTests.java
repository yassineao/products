package com.gloyoo.products;

import com.gloyoo.products.category.entity.Category;
import com.gloyoo.products.category.repository.CategoryRepository;
import com.gloyoo.products.product.entity.Product;
import com.gloyoo.products.product.repository.ProductRepository;
import com.gloyoo.products.productImage.client.SupabaseStorageClient;
import com.gloyoo.products.productImage.entity.ProductImage;
import com.gloyoo.products.productImage.repository.ProductImageRepository;
import com.gloyoo.products.users.client.UserClient;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @MockitoBean
    private SupabaseStorageClient supabaseStorageClient;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void configureMockMvc() {
        mockMvc = webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void clearDatabase() {
        productImageRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
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
    void productImagesArePublic() throws Exception {
        mockMvc.perform(get("/product-image"))
                .andExpect(status().isOk());

        verifyNoInteractions(userClient);
    }

    @Test
    void staleAccessTokenDoesNotBlockPublicProductImages() throws Exception {
        mockMvc.perform(get("/product-image")
                        .cookie(new Cookie("accessToken", "expired-or-invalid")))
                .andExpect(status().isOk());

        verifyNoInteractions(userClient);
    }

    @Test
    void productImagesIncludeProductIdsAfterRepositorySessionCloses() throws Exception {
        Category category = categoryRepository.save(Category.builder()
                .name("Image test category")
                .active(true)
                .build());
        Product product = productRepository.save(Product.builder()
                .name("Image test product")
                .price(BigDecimal.TEN)
                .quantity(1)
                .active(true)
                .category(category)
                .build());
        ProductImage image = productImageRepository.save(ProductImage.builder()
                .bucketName("products")
                .objectPath("products/test/image.jpg")
                .width(100)
                .height(100)
                .mainImage(true)
                .products(List.of(product))
                .build());

        when(supabaseStorageClient.createSignedUrl(
                anyString(), anyString(), anyString(), anyString(), any()
        )).thenReturn(new SupabaseStorageClient.SignedUrlResponse("/signed/image.jpg"));

        mockMvc.perform(get("/product-image"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$[0].id").value(image.getId().toString()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$[0].productIds[0]").value(product.getId().toString()));
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
