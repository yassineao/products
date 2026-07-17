package com.gloyoo.products.productImage.service;

import com.gloyoo.products.productImage.client.SupabaseStorageClient;
import com.gloyoo.products.productImage.config.SupabaseStorageProperties;
import feign.Feign;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class SupabaseStorageService {
    private final SupabaseStorageProperties supabaseStorageProperties;
    private final SupabaseStorageClient supabaseStorageClient;

    public SupabaseStorageService(SupabaseStorageProperties supabaseStorageProperties) {
        this.supabaseStorageProperties = supabaseStorageProperties;
        this.supabaseStorageClient = Feign.builder()
                .target(SupabaseStorageClient.class, getStorageUrl());
    }

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
                    file.getBytes()
            );
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read image file", exception);
        }

        return new StoredImage(bucketName, objectPath, getPublicUrl(bucketName, objectPath));
    }

    private String getStorageUrl() {
        return supabaseStorageProperties.getUrl().replaceAll("/+$", "") + "/storage/v1";
    }

    private String getPublicUrl(String bucketName, String objectPath) {
        return getStorageUrl() + "/object/public/" + bucketName + "/" + encodeObjectPath(objectPath);
    }

    private String encodeObjectPath(String objectPath) {
        String[] parts = objectPath.split("/");
        for (int index = 0; index < parts.length; index++) {
            parts[index] = URLEncoder.encode(parts[index], StandardCharsets.UTF_8).replace("+", "%20");
        }
        return String.join("/", parts);
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
            String objectPath,
            String publicUrl
    ) {
    }
}
