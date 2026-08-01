package com.gloyoo.products.category.controller;

import com.gloyoo.products.category.dto.CategoryRequest;
import com.gloyoo.products.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<?> AddCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        categoryService.AddCategory(categoryRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok().body(categoryService.getAllCategories());
    }

    @PatchMapping
    public ResponseEntity<?> updateCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        categoryService.Update(categoryRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{categoryName}")
    public ResponseEntity<?> deleteCategory(@PathVariable("categoryName") String categoryName) {
        categoryService.DeleteCategory(categoryName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveCategories() {
        return ResponseEntity.ok().body(categoryService.getCategoryByActive());
    }


    @GetMapping("/name/{categoryName}")
    public ResponseEntity<?> getCategoryByName(@PathVariable String categoryName) {
        return ResponseEntity.ok().body(categoryService.getCategoryByName(categoryName));
    }
}
