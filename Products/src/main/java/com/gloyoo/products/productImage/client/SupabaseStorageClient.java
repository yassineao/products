package com.gloyoo.products.productImage.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface SupabaseStorageClient {
    @RequestLine("POST /object/{bucketName}/{objectPath}")
    @Headers({
            "Authorization: {authorization}",
            "apikey: {apiKey}",
            "Content-Type: {contentType}"
    })
    void upload(
            @Param("bucketName") String bucketName,
            @Param("objectPath") String objectPath,
            @Param("authorization") String authorization,
            @Param("apiKey") String apiKey,
            @Param("contentType") String contentType,
            byte[] file
    );
}
