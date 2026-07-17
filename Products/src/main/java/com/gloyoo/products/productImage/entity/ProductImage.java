package com.gloyoo.products.productImage.entity;

import com.gloyoo.products.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private String bucketName;

    @Column(nullable = false)
    private String objectPath;

    @Column(nullable = false)
    private Integer width;

    @Column(nullable = false)
    private Integer height;

    @Column(nullable = false)
    private String publicUrl;

    private String altText;
    private Boolean mainImage;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
