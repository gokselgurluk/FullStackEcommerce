package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.Category;
import com.eticare.eticaretAPI.repository.ICategoryRepository;
import com.eticare.eticaretAPI.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final ICategoryRepository categoryRepository;

    public CategoryServiceImpl(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category createOrUpdate(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long Id) {
        return categoryRepository.findById(Id).orElseThrow(()->new RuntimeException("Category not found with id :"+Id));
    }

    @Override
    public List<Category> getAllCategoryName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> getCategoriesByParentId(Long parentId) {
        return categoryRepository. findByParentCategory_Id(parentId);
    }

    @Override
    public void deleteCategory(Long Id) {
        if (categoryRepository.existsById(Id)) {
            categoryRepository.deleteById(Id);
        }else{
            throw new RuntimeException("Category not found with id : "+Id);
        }

    }
}
