package com.gloyoo.products.product.service;

import com.gloyoo.products.category.entity.Category;
import com.gloyoo.products.category.repository.CategoryRepository;
import com.gloyoo.products.product.dto.ProductRequest;
import com.gloyoo.products.product.entity.Product;
import com.gloyoo.products.product.repository.ProductRepository;
import com.gloyoo.products.productImage.service.ProductImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductImageService productImageService;

    @InjectMocks
    private ProductService productService;

    @Captor
    private ArgumentCaptor<List<Product>> productsCaptor;

    @Test
    void addsProductsAsOneBatch() {
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder().id(categoryId).name("Books").build();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        productService.AddProducts(List.of(
                request("First", categoryId),
                request("Second", categoryId)
        ));

        verify(productRepository).saveAll(productsCaptor.capture());
        assertThat(productsCaptor.getValue())
                .extracting(Product::getName)
                .containsExactly("First", "Second");
        assertThat(productsCaptor.getValue())
                .extracting(Product::getCategory)
                .containsOnly(category);
    }

    @Test
    void doesNotSavePartialBatchWhenACategoryDoesNotExist() {
        UUID existingCategoryId = UUID.randomUUID();
        UUID missingCategoryId = UUID.randomUUID();
        Category category = Category.builder().id(existingCategoryId).name("Books").build();
        when(categoryRepository.findById(existingCategoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(missingCategoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.AddProducts(List.of(
                request("First", existingCategoryId),
                request("Second", missingCategoryId)
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category not found");

        verify(productRepository, never()).saveAll(anyList());
    }

    private ProductRequest request(String name, UUID categoryId) {
        return new ProductRequest(
                name,
                name + " description",
                new BigDecimal("10.00"),
                1,
                new String[]{"test"},
                true,
                categoryId
        );
    }
}
