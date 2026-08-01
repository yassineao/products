package com.gloyoo.products.category.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name="category"
)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id")
    private UUID id;
    @NonNull
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    @Builder.Default
    private boolean active = false;
}
