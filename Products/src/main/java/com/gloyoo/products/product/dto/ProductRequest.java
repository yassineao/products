package com.gloyoo.products.product.dto;

import java.util.UUID;

public record ProductRequest(
        String name,
        String description,
        double price,
        int quantity,
        String[] tags,
        Boolean active,
        UUID categoryId
) {
}
