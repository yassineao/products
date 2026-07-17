package com.gloyoo.products.category.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CategoryRequest(
        @NotNull
        @Size(  min = 3,
                max = 60
        )
        String name,
        @Size(  min = 10,
                max = 800,
                message = "Description must contain between 10 and 800 characters")
        String description,
        boolean active

) {
}
