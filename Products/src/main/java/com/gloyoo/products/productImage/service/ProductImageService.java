package com.gloyoo.products.productImage.service;

import com.gloyoo.products.product.entity.Product;
import com.gloyoo.products.product.repository.ProductRepository;
import com.gloyoo.products.productImage.dto.ProductImageRequest;
import com.gloyoo.products.productImage.dto.ProductImageResponse;
import com.gloyoo.products.productImage.entity.ProductImage;
import com.gloyoo.products.productImage.repository.ProductImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.List;
import java.util.UUID;

@Service
public class ProductImageService {
    final private ProductImageRepository productImageRepository;
    final private ProductRepository productRepository;
    final private SupabaseStorageService supabaseStorageService;

    public ProductImageService(
            ProductImageRepository productImageRepository,
            ProductRepository productRepository,
            SupabaseStorageService supabaseStorageService
    ) {
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    public ProductImageResponse UploadProductImage(UUID productId, MultipartFile file, String altText, Boolean mainImage) {
        Product product = getProduct(productId);
        SupabaseStorageService.StoredImage storedImage = supabaseStorageService.uploadProductImage(productId, file);
        ImageSize imageSize = getImageSize(file);

        ProductImage productImage = ProductImage.builder()
                .bucketName(storedImage.bucketName())
                .objectPath(storedImage.objectPath())
                .width(imageSize.width())
                .height(imageSize.height())
                .altText(altText)
                .mainImage(mainImage)
                .products(List.of(product))
                .build();

        return toResponse(productImageRepository.save(productImage));
    }

    public void UpdateProductImage(UUID id, ProductImageRequest productImageRequest) {
        ProductImage productImage = findById(id);
        productImage.setAltText(productImageRequest.altText());
        productImage.setMainImage(productImageRequest.mainImage());

        productImageRepository.save(productImage);
    }

    public void DeleteProductImage(UUID id) {
        ProductImage productImage = findById(id);
        supabaseStorageService.deleteProductImage(productImage.getBucketName(), productImage.getObjectPath());
        productImageRepository.delete(productImage);
    }

    @Transactional
    public void DeleteProductImagesByProduct(UUID productId) {
        List<ProductImage> productImages = productImageRepository.findAllByProductsId(productId);

        productImages.forEach(productImage -> {
            productImage.getProducts().removeIf(product -> product.getId().equals(productId));
            if (!productImage.getProducts().isEmpty()) {
                productImageRepository.save(productImage);
                return;
            }

            // Delete the stored file only when no other product uses the image.

                supabaseStorageService.deleteProductImage(
                        productImage.getBucketName(),
                        productImage.getObjectPath()
                );
            productImageRepository.delete(productImage);
        });
    }

    public List<ProductImageResponse> getAllProductImages() {
        return productImageRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductImageResponse> getProductImagesByProduct(UUID productId) {
        getProduct(productId);
        return productImageRepository.findAllByProductsId(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductImageResponse getProductImageById(UUID id) {
        return toResponse(findById(id));
    }

    public ProductImage findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return productImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product image not found"));
    }

    private Product getProduct(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public ProductImageResponse toResponse(ProductImage productImage) {
        return new ProductImageResponse(
                productImage.getId(),
                productImage.getBucketName(),
                productImage.getObjectPath(),
                productImage.getWidth(),
                productImage.getHeight(),
                supabaseStorageService.createSignedUrl(
                        productImage.getBucketName(),
                        productImage.getObjectPath()
                ),
                productImage.getAltText(),
                productImage.getMainImage(),
                productImage.getProducts().stream()
                        .map(Product::getId)
                        .toList()
        );
    }

    private ImageSize getImageSize(MultipartFile file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (bufferedImage == null) {
                throw new IllegalArgumentException("File is not a valid image");
            }
            return new ImageSize(bufferedImage.getWidth(), bufferedImage.getHeight());
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read image dimensions", exception);
        }
    }

    private record ImageSize(
            Integer width,
            Integer height
    ) {
    }
}
