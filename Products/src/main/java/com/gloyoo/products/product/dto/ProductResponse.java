package com.gloyoo.products.product.dto;

import com.gloyoo.products.category.entity.Category;
import com.gloyoo.products.productImage.dto.ProductImageResponse;

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
        Category category,
        List<ProductImageResponse> images
) {
}
