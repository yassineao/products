package com.gloyoo.products.productImage.service;

import com.gloyoo.products.product.entity.Product;
import com.gloyoo.products.product.repository.ProductRepository;
import com.gloyoo.products.productImage.dto.ProductImageRequest;
import com.gloyoo.products.productImage.entity.ProductImage;
import com.gloyoo.products.productImage.repository.ProductImageRepository;
import org.springframework.stereotype.Service;
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

    public void AddProductImage(ProductImageRequest productImageRequest) {
        Product product = getProduct(productImageRequest.productId());

        ProductImage productImage = ProductImage.builder()
                .bucketName(productImageRequest.bucketName())
                .objectPath(productImageRequest.objectPath())
                .width(productImageRequest.width())
                .height(productImageRequest.height())
                .publicUrl(productImageRequest.publicUrl())
                .altText(productImageRequest.altText())
                .mainImage(productImageRequest.mainImage())
                .product(product)
                .build();

        productImageRepository.save(productImage);
    }

    public ProductImage UploadProductImage(UUID productId, MultipartFile file, String altText, Boolean mainImage) {
        Product product = getProduct(productId);
        SupabaseStorageService.StoredImage storedImage = supabaseStorageService.uploadProductImage(productId, file);
        ImageSize imageSize = getImageSize(file);

        ProductImage productImage = ProductImage.builder()
                .bucketName(storedImage.bucketName())
                .objectPath(storedImage.objectPath())
                .width(imageSize.width())
                .height(imageSize.height())
                .publicUrl(storedImage.publicUrl())
                .altText(altText)
                .mainImage(mainImage)
                .product(product)
                .build();

        return productImageRepository.save(productImage);
    }

    public void UpdateProductImage(UUID id, ProductImageRequest productImageRequest) {
        ProductImage productImage = findById(id);
        Product product = getProduct(productImageRequest.productId());

        productImage.setBucketName(productImageRequest.bucketName());
        productImage.setObjectPath(productImageRequest.objectPath());
        productImage.setWidth(productImageRequest.width());
        productImage.setHeight(productImageRequest.height());
        productImage.setPublicUrl(productImageRequest.publicUrl());
        productImage.setAltText(productImageRequest.altText());
        productImage.setMainImage(productImageRequest.mainImage());
        productImage.setProduct(product);

        productImageRepository.save(productImage);
    }

    public void DeleteProductImage(UUID id) {
        ProductImage productImage = findById(id);

        productImageRepository.delete(productImage);
    }

    public List<ProductImage> getAllProductImages() {
        return productImageRepository.findAll();
    }

    public List<ProductImage> getProductImagesByProduct(UUID productId) {
        getProduct(productId);
        return productImageRepository.findAllByProductId(productId);
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
