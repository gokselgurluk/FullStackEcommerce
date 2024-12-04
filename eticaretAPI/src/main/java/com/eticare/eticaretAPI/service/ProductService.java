package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    //ürün ekle veya güncelle
    Product createOrUpdate (Product product);
    //tüm ürünleri getir
    List<Product> getAllProduct();
    //id ye göre ürün getir
    Optional<Product> getProductById(Long id);
    //Kategorisine göre ürünleri getir
    List<Product> getProductsByCategory(Long categoryId);
    //ürün sil
    void deleteProduct(Long id);
}
