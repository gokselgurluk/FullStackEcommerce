package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.entity.Category;
import com.eticare.eticaretAPI.service.CategoryService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    ResponseEntity<List<Category>> getAllCategory() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createOrUpdate(category);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<Category> updateCAtegory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return ResponseEntity.ok(categoryService.createOrUpdate(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }


    // Get categories by parent ID
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Category>> getCategoriesByParentId(@PathVariable Long parentId) {
        List<Category> categories = categoryService.getCategoriesByParentId(parentId);
        return ResponseEntity.ok(categories);
    }


    // Get categories by name
    @GetMapping("/search")
    public ResponseEntity<List<Category>> getCategoriesByName(@RequestParam String name) {
        List<Category> categories = categoryService.getAllCategoryName(name);
        return ResponseEntity.ok(categories);
    }
}



