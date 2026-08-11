package com.gloyoo.cart.carts.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CartInitRequest(
        @NotNull UUID userId
) {

}
