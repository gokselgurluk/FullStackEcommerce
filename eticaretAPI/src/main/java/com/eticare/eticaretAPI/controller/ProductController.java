package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.ModelMapper.ModelMapperServiceImpl;
import com.eticare.eticaretAPI.dto.request.Product.ProductSaveRequest;
import com.eticare.eticaretAPI.dto.request.Product.ProductUpdateRequest;
import com.eticare.eticaretAPI.dto.response.ProductResponse;
import com.eticare.eticaretAPI.entity.Product;
import com.eticare.eticaretAPI.service.ProductService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private  final ModelMapperServiceImpl modelMapperService;

    @Autowired
    public ProductController(ProductService productService, ModelMapperServiceImpl modelMapperService) {
        this.productService = productService;
        this.modelMapperService = modelMapperService;
    }

    @GetMapping
    ResponseEntity<List<ProductResponse>> getAllProducts(){
        List<Product> products=productService.getAllProduct();
        List<ProductResponse> response=products.stream().map(Product->this.modelMapperService.forResponse().map(Product,ProductResponse.class)).collect(Collectors.toList());
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> getProductById(@PathVariable Long id){
        Optional<Product> product=productService.getProductById(id);
        return product.map(Product->ResponseEntity.ok(this.modelMapperService.forResponse().map(Product,ProductResponse.class))).orElse(ResponseEntity.notFound().build());
        //Optional ile gelen veriyi kontrol etmek ve map fonksiyonunu kullanarak ProductResponse sınıfına dönüştürmek temiz ve modern bir yaklaşımdır.
    }


    @PostMapping("/create")
    ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductSaveRequest productSaveRequest){
        Product product = this.modelMapperService.forRequest().map(productSaveRequest,Product.class);
        productService.createOrUpdate(product);
        ProductResponse response = this.modelMapperService.forResponse().map(product,ProductResponse.class);
        return  new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PutMapping("/update")
    ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductUpdateRequest productUpdateRequest){
        Product product = this.modelMapperService.forRequest().map(productUpdateRequest,Product.class);
        productService.createOrUpdate(product);
        ProductResponse response = this.modelMapperService.forResponse().map(product,ProductResponse.class);
        return  new ResponseEntity<>(response, HttpStatus.valueOf("Update completed"));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
