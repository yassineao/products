package com.gloyoo.cart.carts.Service;

import com.gloyoo.cart.carts.client.ProductClient;
import com.gloyoo.cart.carts.dto.ProductReference;
import com.gloyoo.cart.carts.entity.Carts;
import com.gloyoo.cart.carts.exception.CartNotFoundException;
import com.gloyoo.cart.carts.repository.CartRepository;
import com.gloyoo.cart.configuration.AuthenticatedUser;
import com.gloyoo.cart.users.client.UserClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CartServiceTests {
    private CartRepository cartRepository;
    private ProductClient productClient;
    private UserClient userClient;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        productClient = mock(ProductClient.class);
        userClient = mock(UserClient.class);
        cartService = new CartService(cartRepository, productClient, userClient);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void initializesCartForAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        authenticate(userId);
        when(cartRepository.save(org.mockito.ArgumentMatchers.any(Carts.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Carts cart = cartService.initializeCartForCurrentUser();

        assertThat(cart.getUserId()).isEqualTo(userId);
        assertThat(cart.getProductIds()).isEmpty();
        verify(cartRepository).save(cart);
    }

    @Test
    void rejectsCartInitializationWhenUserIsNotAuthenticated() {
        assertThatThrownBy(() -> cartService.initializeCartForCurrentUser())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User is not authenticated");

        verifyNoInteractions(cartRepository, userClient);
    }

    @Test
    void addsValidatedProductsWithoutReplacingOrDuplicatingExistingProducts() {
        UUID userId = UUID.randomUUID();
        UUID existingProduct = UUID.randomUUID();
        UUID newProduct = UUID.randomUUID();
        Carts cart = Carts.builder().userId(userId).productIds(new UUID[]{existingProduct}).build();
        authenticate(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productClient.getProductById(newProduct)).thenReturn(new ProductReference(newProduct));
        when(cartRepository.save(cart)).thenReturn(cart);

        Carts updated = cartService.addProductsToCart(Set.of(newProduct, existingProduct));

        assertThat(updated.getProductIds()).containsExactly(existingProduct, newProduct);
        verify(productClient).getProductById(newProduct);
        verify(productClient).getProductById(existingProduct);
    }

    @Test
    void deletesOnlyRequestedProducts() {
        UUID userId = UUID.randomUUID();
        UUID retainedProduct = UUID.randomUUID();
        UUID removedProduct = UUID.randomUUID();
        Carts cart = Carts.builder()
                .userId(userId)
                .productIds(new UUID[]{retainedProduct, removedProduct})
                .build();
        authenticate(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        Carts updated = cartService.deleteProductsFromCart(Set.of(removedProduct));

        assertThat(updated.getProductIds()).containsExactly(retainedProduct);
        verify(cartRepository).save(cart);
    }

    @Test
    void doesNotExposeAnotherUsersCart() {
        UUID currentUserId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        authenticate(currentUserId);
        when(cartRepository.findByIdAndUserId(cartId, currentUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.findCartById(cartId))
                .isInstanceOf(CartNotFoundException.class);

        verify(cartRepository, never()).findById(cartId);
    }

    @Test
    void doesNotDeleteAnotherUsersCart() {
        UUID currentUserId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        authenticate(currentUserId);
        when(cartRepository.findByIdAndUserId(cartId, currentUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.deleteCart(cartId))
                .isInstanceOf(CartNotFoundException.class);

        verify(cartRepository, never()).delete(org.mockito.ArgumentMatchers.any(Carts.class));
    }

    private void authenticate(UUID userId) {
        AuthenticatedUser principal = new AuthenticatedUser(
                userId,
                "test@example.com",
                "Test User",
                "USER"
        );
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(principal, null, Set.of())
        );
    }
}
