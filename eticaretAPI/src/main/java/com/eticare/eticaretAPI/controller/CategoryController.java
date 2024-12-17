package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.ModelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.response.CategoryResponse;
import com.eticare.eticaretAPI.entity.Category;
import com.eticare.eticaretAPI.service.CategoryService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private  final IModelMapperService modelMapperService ;

    public CategoryController(CategoryService categoryService, IModelMapperService modelMapperService) {
        this.categoryService = categoryService;
        this.modelMapperService = modelMapperService;
    }

    @GetMapping
    ResponseEntity<List<CategoryResponse>> getAllCategory() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponse> response =categories.stream().map(C->this.modelMapperService.forResponse().map(C,CategoryResponse.class)).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Category category= categoryService.getCategoryById(id);
        CategoryResponse response = this.modelMapperService.forResponse().map(category,CategoryResponse.class);
        return ResponseEntity.ok(response);
    }

    // Get categories by parent ID
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByParentId(@PathVariable Long parentId) {
        List<Category> categories = categoryService.getCategoriesByParentId(parentId);
        List<CategoryResponse> response = categories.stream().map(C->this.modelMapperService.forResponse().map(C,CategoryResponse.class)).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


    // Get categories by name
    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByName(@RequestParam String name) {
        List<Category> categories = categoryService.getAllCategoryName(name);
        List<CategoryResponse> response = categories.stream().map(C->this.modelMapperService.forResponse().map(C,CategoryResponse.class)).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    ResponseEntity<CategoryResponse> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createOrUpdate(category);
        CategoryResponse response = this.modelMapperService.forResponse().map(createdCategory,CategoryResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<CategoryResponse> updateCAtegory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        Category categoryUpdate =categoryService.createOrUpdate(category);
        CategoryResponse response = this.modelMapperService.forResponse().map(categoryUpdate,CategoryResponse.class);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }


}



