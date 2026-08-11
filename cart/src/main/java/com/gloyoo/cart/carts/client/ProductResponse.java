package com.gloyoo.cart.carts.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        int quantity,
        String[] tags,
        Boolean active,
        Object category,
        List<?> images

) {
}
