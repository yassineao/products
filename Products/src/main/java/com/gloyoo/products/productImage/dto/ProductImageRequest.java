package com.gloyoo.products.productImage.dto;

public record ProductImageRequest(
        String altText,
        Boolean mainImage
) {
}
