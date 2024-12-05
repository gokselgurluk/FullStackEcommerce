package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICategoryRepository extends JpaRepository<Category,Long> {

    List<Category> findByName(String name);

    List<Category> findByParentCategory_Id(Long parentId);
}
