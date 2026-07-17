package com.gloyoo.products.productImage.controller;

import com.gloyoo.products.productImage.dto.ProductImageRequest;
import com.gloyoo.products.productImage.service.ProductImageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/product-image")
public class ProductImageController {
    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping
    public ResponseEntity<?> addProductImage(@Valid @RequestBody ProductImageRequest productImageRequest) {
        productImageService.AddProductImage(productImageRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProductImage(
            @RequestParam UUID productId,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String altText,
            @RequestParam(required = false) Boolean mainImage
    ) {
        return ResponseEntity.ok().body(productImageService.UploadProductImage(productId, file, altText, mainImage));
    }

    @GetMapping
    public ResponseEntity<?> getAllProductImages() {
        return ResponseEntity.ok().body(productImageService.getAllProductImages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductImageById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(productImageService.findById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductImagesByProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok().body(productImageService.getProductImagesByProduct(productId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProductImage(
            @PathVariable UUID id,
            @Valid @RequestBody ProductImageRequest productImageRequest
    ) {
        productImageService.UpdateProductImage(id, productImageRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductImage(@PathVariable UUID id) {
        productImageService.DeleteProductImage(id);
        return ResponseEntity.ok().build();
    }
}
