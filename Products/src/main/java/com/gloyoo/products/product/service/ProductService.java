package com.gloyoo.products.product.service;

import com.gloyoo.products.category.entity.Category;
import com.gloyoo.products.category.repository.CategoryRepository;
import com.gloyoo.products.product.dto.ProductRequest;
import com.gloyoo.products.product.dto.ProductResponse;
import com.gloyoo.products.product.entity.Product;
import com.gloyoo.products.product.repository.ProductRepository;
import com.gloyoo.products.productImage.service.ProductImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    final private ProductRepository productRepository;
    final private CategoryRepository categoryRepository;
    final private ProductImageService productImageService;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductImageService productImageService
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageService = productImageService;
    }

    public void AddProduct(ProductRequest productRequest) {
        Category category = getCategory(productRequest.categoryId());

        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .category(category)
                .tags(productRequest.tags())
                .active(productRequest.active())
                .quantity(productRequest.quantity())
                .build();

        productRepository.save(product);
    }

    public void DeleteProduct(UUID id) {
        Product product = findById(id);
        productImageService.DeleteProductImagesByProduct(id);
        productRepository.delete(product);
    }

    public void UpdateProduct(UUID id, ProductRequest productRequest) {
        Product product = findById(id);
        Category category = getCategory(productRequest.categoryId());

        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());
        product.setCategory(category);
        product.setTags(productRequest.tags());
        product.setActive(productRequest.active());
        product.setQuantity(productRequest.quantity());

        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findAllByActive(true).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        return toResponse(findById(id));
    }

    public Product findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    private Category getCategory(UUID categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category id cannot be null");
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getTags(),
                product.getActive(),
                product.getCategory(),
                product.getImages().stream()
                        .map(productImageService::toResponse)
                        .toList()
        );
    }
}
