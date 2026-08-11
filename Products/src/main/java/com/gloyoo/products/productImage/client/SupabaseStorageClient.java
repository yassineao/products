package com.gloyoo.products.productImage.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "supabase-storage-service",
        url = "${supabase.storage.url}"
)
public interface SupabaseStorageClient {

    @PostMapping("/storage/v1/object/{bucketName}/{objectPath}")
    ResponseEntity<String> upload(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("objectPath") String objectPath,

            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization,

            @RequestHeader("apikey")
            String apiKey,

            @RequestHeader(HttpHeaders.CONTENT_TYPE)
            String contentType,

            @RequestHeader(value = "x-upsert", required = false)
            String upsert,

            @RequestBody
            byte[] file
    );

    @DeleteMapping("/storage/v1/object/{bucketName}/{objectPath}")
    ResponseEntity<String> delete(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("objectPath") String objectPath,

            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization,

            @RequestHeader("apikey")
            String apiKey
    );

    @PostMapping("/storage/v1/object/sign/{bucketName}/{objectPath}")
    SignedUrlResponse createSignedUrl(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("objectPath") String objectPath,

            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization,

            @RequestHeader("apikey")
            String apiKey,

            @RequestBody
            SignedUrlRequest request
    );

    record SignedUrlRequest(long expiresIn) {
    }

    record SignedUrlResponse(@JsonProperty("signedURL") String signedUrl) {
    }
}
