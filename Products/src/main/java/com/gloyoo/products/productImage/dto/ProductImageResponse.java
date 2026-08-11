package com.gloyoo.products.productImage.dto;

import java.util.List;
import java.util.UUID;

public record ProductImageResponse(
        UUID id,
        String bucketName,
        String objectPath,
        Integer width,
        Integer height,
        String signedUrl,
        String altText,
        Boolean mainImage,
        List<UUID> productIds
) {
}
