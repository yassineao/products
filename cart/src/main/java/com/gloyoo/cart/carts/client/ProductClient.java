package com.gloyoo.cart.carts.client;

import com.gloyoo.cart.carts.dto.ProductReference;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-service", url = "${product-service.url}")
public interface ProductClient {
    @GetMapping("/product/{id}")
    ProductReference getProductById(@PathVariable("id") UUID id);
}
