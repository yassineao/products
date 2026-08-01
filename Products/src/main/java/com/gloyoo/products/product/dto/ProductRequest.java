package com.gloyoo.products.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(
        String name,
        String description,
        BigDecimal price,
        int quantity,
        String[] tags,
        Boolean active,
        UUID categoryId
) {
}
