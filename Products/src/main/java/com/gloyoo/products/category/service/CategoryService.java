package com.gloyoo.products.category.service;

import com.gloyoo.products.category.dto.CategoryRequest;
import com.gloyoo.products.category.entity.Category;
import com.gloyoo.products.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    final private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void AddCategory(CategoryRequest categoryRequest) {
        if(categoryRepository.existsByName(categoryRequest.name())) {
            throw new IllegalArgumentException(
                    "a category with name " + categoryRequest.name() + " already exists"
            );
        }

        Category category = Category.builder()
                .name(categoryRequest.name())
                .description(categoryRequest.description())
                .active(categoryRequest.active())
                .build();
        categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();

    }

    public void Update (CategoryRequest categoryRequest) {
        Category category = this.getCategoryByName(categoryRequest.name());
        category.setName(categoryRequest.name());
        category.setDescription(categoryRequest.description());
        category.setActive(categoryRequest.active());
        categoryRepository.save(category);
    }


    public void DeleteCategory(String name) {
        Category category = this.getCategoryByName(name);

        categoryRepository.delete(category);
    }



    public Category getCategoryByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return categoryRepository.getCategoryByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    public List<Category> getCategoryByActive() {
        return categoryRepository.findAllByActive(true);
    }
}
