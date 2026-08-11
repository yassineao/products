package com.gloyoo.cart.carts.controller;

import com.gloyoo.cart.carts.Service.CartService;
import com.gloyoo.cart.carts.entity.Carts;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Carts> findCurrentUserCart() {
        return ResponseEntity.ok(cartService.findCurrentUserCart());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carts> findCart(@PathVariable UUID id) {
        return ResponseEntity.ok(cartService.findCartById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable UUID id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Carts> initialize() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.initializeCartForCurrentUser());
    }

    @PostMapping("/addProducts")
    public ResponseEntity<Carts> addProductsToCart(@RequestBody Set<UUID> productIds) {
        return ResponseEntity.ok(cartService.addProductsToCart(productIds));
    }

    @DeleteMapping("/deleteProducts")
    public ResponseEntity<Carts> deleteProductsToCart(@RequestBody Set<UUID> productIds) {
        return ResponseEntity.ok(cartService.deleteProductsFromCart(productIds));
    }
}
