package com.gloyoo.products.productImage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "supabase.storage")
public class SupabaseStorageProperties {
    private String url;
    private String serviceKey;
    private String bucket = "product-images";

}
