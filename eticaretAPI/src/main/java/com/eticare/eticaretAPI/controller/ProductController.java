package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.entity.Product;
import com.eticare.eticaretAPI.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    ResponseEntity<List<Product>> getAllProducts(){
        return  ResponseEntity.ok(productService.getAllProduct());
    }

    @GetMapping("/{id}")
    ResponseEntity<Optional<Product>> getProductById(@RequestParam Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    ResponseEntity<Product> creatOrUpdate(@RequestBody Product product){
        Product productStatus = productService.createOrUpdate(product);
        return  new ResponseEntity<>(productStatus, HttpStatus.CREATED);
    }
}
