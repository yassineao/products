package com.gloyoo.cart.carts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class CartAlreadyExistsException extends RuntimeException {
    public CartAlreadyExistsException(UUID userId) {
        super("A cart already exists for user " + userId);
    }
}
