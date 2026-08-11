package com.gloyoo.cart.carts.Service;

import com.gloyoo.cart.carts.client.ProductClient;
import com.gloyoo.cart.carts.entity.Carts;
import com.gloyoo.cart.carts.exception.CartAlreadyExistsException;
import com.gloyoo.cart.carts.exception.CartNotFoundException;
import com.gloyoo.cart.carts.repository.CartRepository;
import com.gloyoo.cart.configuration.AuthenticatedUser;
import com.gloyoo.cart.users.client.UserClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final UserClient userClient;

    public CartService(
            CartRepository cartRepository,
            ProductClient productClient,
            UserClient userClient
    ) {
        this.cartRepository = cartRepository;
        this.productClient = productClient;
        this.userClient = userClient;
    }

    public Carts addProductsToCart(Set<UUID> productIds) {
        Set<UUID> productIdsToAdd = requireProductIds(productIds);
        Carts cart = findCurrentUserCart();
        Set<UUID> products = new LinkedHashSet<>(Arrays.asList(safeProductIds(cart.getProductIds())));

        for (UUID productId : productIdsToAdd) {
            productClient.getProductById(productId);
            products.add(productId);
        }

        cart.setProductIds(products.toArray(UUID[]::new));
        return cartRepository.save(cart);
    }

    public Carts deleteProductsFromCart(Set<UUID> productIds) {
        Set<UUID> productsToDelete = requireProductIds(productIds);
        Carts cart = findCurrentUserCart();
        UUID[] remainingProducts = Arrays.stream(safeProductIds(cart.getProductIds()))
                .filter(productId -> !productsToDelete.contains(productId))
                .toArray(UUID[]::new);

        cart.setProductIds(remainingProducts);
        return cartRepository.save(cart);
    }

    public Carts initializeCartForCurrentUser() {
        UUID userId = getCurrentUserId();

        if (cartRepository.existsByUserId(userId)) {
            throw new CartAlreadyExistsException(userId);
        }

        Carts cart = Carts.builder()
                .userId(userId)
                .productIds(new UUID[0])
                .amount(BigInteger.ZERO)
                .build();
        return cartRepository.save(cart);
    }

    public void deleteCart(UUID cartId) {
        cartRepository.delete(findCartById(cartId));
    }

    @Transactional(readOnly = true)
    public Carts findCurrentUserCart() {
        UUID userId = getCurrentUserId();
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
    }

    @Transactional(readOnly = true)
    public Carts findCartById(UUID cartId) {
        Objects.requireNonNull(cartId, "Cart id is required");
        return cartRepository.findByIdAndUserId(cartId, getCurrentUserId())
                .orElseThrow(() -> new CartNotFoundException(cartId));
    }

    public void addToCartProductSum(BigInteger price) {
        Objects.requireNonNull(price, "Product price is required");
        Carts cart = findCurrentUserCart();
        BigInteger sum = Objects.requireNonNullElse(cart.getAmount(), BigInteger.ZERO);
        cart.setAmount(sum.add(price));
        cartRepository.save(cart);
    }


    private Set<UUID> requireProductIds(Set<UUID> productIds) {
        Objects.requireNonNull(productIds, "Product ids are required");
        if (productIds.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Product ids cannot contain null values");
        }
        return productIds;
    }

    private AuthenticatedUser getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new IllegalStateException("User is not authenticated");
        }
        return (AuthenticatedUser) authentication.getPrincipal();
    }

    private UUID getCurrentUserId() {
        return Objects.requireNonNull(getCurrentUser().id(), "Authenticated user id is required");
    }
    private UUID[] safeProductIds(UUID[] productIds) {
        return productIds == null ? new UUID[0] : productIds;
    }
}
