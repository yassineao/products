package com.gloyoo.products.productImage.service;

import com.gloyoo.products.productImage.client.SupabaseStorageClient;
import com.gloyoo.products.productImage.config.SupabaseStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SupabaseStorageService {

    private final SupabaseStorageProperties supabaseStorageProperties;

    private final SupabaseStorageClient supabaseStorageClient;

    public StoredImage uploadProductImage(UUID productId, MultipartFile file) {
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }
        if (!StringUtils.hasText(supabaseStorageProperties.getUrl())) {
            throw new IllegalStateException("Supabase storage url is not configured");
        }
        if (!StringUtils.hasText(supabaseStorageProperties.getServiceKey())) {
            throw new IllegalStateException("Supabase storage service key is not configured");
        }

        String bucketName = supabaseStorageProperties.getBucket();
        String objectPath = "products/" + productId + "/" + UUID.randomUUID() + getFileExtension(file);

        try {
            supabaseStorageClient.upload(
                    bucketName,
                    objectPath,
                    "Bearer " + supabaseStorageProperties.getServiceKey(),
                    supabaseStorageProperties.getServiceKey(),
                    getContentType(file),
                    "true",
                    file.getBytes()
            );
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read image file", exception);
        }

        return new StoredImage(bucketName, objectPath);
    }

    public String createSignedUrl(String bucketName, String objectPath) {
        validateStorageAccess(bucketName, objectPath);

        SupabaseStorageClient.SignedUrlResponse response = supabaseStorageClient.createSignedUrl(
                bucketName,
                objectPath,
                "Bearer " + supabaseStorageProperties.getServiceKey(),
                supabaseStorageProperties.getServiceKey(),
                new SupabaseStorageClient.SignedUrlRequest(
                        supabaseStorageProperties.getSignedUrlExpirationSeconds()
                )
        );

        if (response == null || !StringUtils.hasText(response.signedUrl())) {
            throw new IllegalStateException("Supabase did not return a signed image URL");
        }

        String signedUrl = response.signedUrl();
        if (signedUrl.startsWith("http://") || signedUrl.startsWith("https://")) {
            return signedUrl;
        }
        return getStorageUrl() + (signedUrl.startsWith("/") ? signedUrl : "/" + signedUrl);
    }

    public void deleteProductImage(String bucketName, String objectPath) {
        validateStorageAccess(bucketName, objectPath);

        supabaseStorageClient.delete(
                bucketName,
                objectPath,
                "Bearer " + supabaseStorageProperties.getServiceKey(),
                supabaseStorageProperties.getServiceKey()
        );
    }

    private String getStorageUrl() {
        return supabaseStorageProperties.getUrl().replaceAll("/+$", "") + "/storage/v1";
    }

    private void validateStorageAccess(String bucketName, String objectPath) {
        if (!StringUtils.hasText(bucketName) || !StringUtils.hasText(objectPath)) {
            throw new IllegalArgumentException("Image bucket name and object path cannot be empty");
        }
        if (!StringUtils.hasText(supabaseStorageProperties.getUrl())) {
            throw new IllegalStateException("Supabase storage url is not configured");
        }
        if (!StringUtils.hasText(supabaseStorageProperties.getServiceKey())) {
            throw new IllegalStateException("Supabase storage service key is not configured");
        }
    }

    private String getContentType(MultipartFile file) {
        if (StringUtils.hasText(file.getContentType())) {
            return file.getContentType();
        }
        return "application/octet-stream";
    }

    private String getFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return extension.replaceAll("[^a-z0-9.]", "");
    }

    public record StoredImage(
            String bucketName,
            String objectPath
    ) {
    }
}
