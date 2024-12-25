package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.config.result.Result;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.Category.CategorySaveRequest;
import com.eticare.eticaretAPI.dto.request.Category.CategoryUpdateRequest;
import com.eticare.eticaretAPI.dto.response.CategoryResponse;
import com.eticare.eticaretAPI.entity.Category;
import com.eticare.eticaretAPI.service.CategoryService;
import jakarta.validation.Valid;
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
    public ResultData<List<CategoryResponse>> getAllCategory() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponse> response =categories.stream().map(C->this.modelMapperService.forResponse().map(C,CategoryResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);
    }

    @GetMapping("/{id}")
    public ResultData<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Category category= categoryService.getCategoryById(id);
        CategoryResponse response = this.modelMapperService.forResponse().map(category,CategoryResponse.class);
        return ResultHelper.success(response);
    }

    // Get categories by parent ID
    @GetMapping("/parent/{parentId}")
    public ResultData<List<CategoryResponse>> getCategoriesByParentId(@PathVariable Long parentId) {
        List<Category> categories = categoryService.getCategoriesByParentId(parentId);
        List<CategoryResponse> response = categories.stream().map(C->this.modelMapperService.forResponse().map(C,CategoryResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);
    }


    // Get categories by name
    @GetMapping("/search")
    public ResultData<List<CategoryResponse>> getCategoriesByName(@RequestParam String name) {
        List<Category> categories = categoryService.getAllCategoryName(name);
        List<CategoryResponse> response = categories.stream().map(C->this.modelMapperService.forResponse().map(C,CategoryResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);
    }

    @PostMapping("/create")
    public ResultData<CategoryResponse> createCategory(@RequestBody @Valid  CategorySaveRequest  categorySaveRequest) {
        Category createdCategory = this.modelMapperService.forRequest().map(categorySaveRequest,Category.class);
        categoryService.createOrUpdate(createdCategory);
        CategoryResponse response = this.modelMapperService.forResponse().map(createdCategory,CategoryResponse.class);
        return ResultHelper.created(response);
    }

    @PutMapping("/update")
    public ResultData<CategoryResponse> updateCAtegory(@RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest) {
        Category updateCategory =this.modelMapperService.forResponse().map(categoryUpdateRequest,Category.class);
        categoryService.createOrUpdate(updateCategory);
        CategoryResponse response = this.modelMapperService.forResponse().map(updateCategory,CategoryResponse.class);
        return ResultHelper.success(response);
    }


    @DeleteMapping("/{id}")
    public Result deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResultHelper.Ok();
    }


}



