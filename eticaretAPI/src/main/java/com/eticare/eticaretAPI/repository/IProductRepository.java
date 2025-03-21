package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product,Long> {

    List<Product> getByCategoryId(Long categoryId);
}
