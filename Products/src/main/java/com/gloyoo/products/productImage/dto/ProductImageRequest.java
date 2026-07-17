package com.gloyoo.products.productImage.dto;

import java.util.UUID;

public record ProductImageRequest(
        UUID productId,
        String bucketName,
        String objectPath,
        Integer width,
        Integer height,
        String publicUrl,
        String altText,
        Boolean mainImage
) {
}
