package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.Product;
import com.eticare.eticaretAPI.repository.IProductRepository;
import com.eticare.eticaretAPI.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {


    private  final IProductRepository productRepository;

    public ProductServiceImpl(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createOrUpdate(Product product) {
        if (product.getId() == null || !productRepository.existsById(product.getId()))
        { // ID'si olmayan bir ürün yeni olarak kaydedilir
            return productRepository.save(product);
        }else{
            return productRepository.save(product);
        }

    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.getByCategoryId(categoryId);
    }

    @Override
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)){//id var mı kontrol et
            productRepository.deleteById(id);//id var ise sil
        }else{
            throw new RuntimeException("Product not found with id: "+id);// id yok ise hata mesajı fırlat
        }


    }
}
