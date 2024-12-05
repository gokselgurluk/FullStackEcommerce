package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.Category;

import java.util.List;

public interface CategoryService {

    Category createOrUpdate (Category category);
    List<Category> getAllCategories();
    Category getCategoryById (Long Id);
    List<Category> getAllCategoryName(String name);

    List<Category> getCategoriesByParentId(Long parentId);

    void deleteCategory(Long Id);

}
