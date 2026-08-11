package com.gloyoo.products.productImage.entity;

import com.gloyoo.products.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "product_image"
)
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String bucketName;

    @Column(nullable = false)
    private String objectPath;

    @Column(nullable = false)
    private Integer width;

    @Column(nullable = false)
    private Integer height;

    private String altText;
    private Boolean mainImage;

    @ManyToMany
    @JoinTable(
            name = "product_image_product",
            joinColumns = @JoinColumn(name = "product_image_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Product> products = new ArrayList<>();
}
