package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.modelMapper.ModelMapperServiceImpl;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.Product.ProductSaveRequest;
import com.eticare.eticaretAPI.dto.request.Product.ProductUpdateRequest;
import com.eticare.eticaretAPI.dto.response.ProductResponse;
import com.eticare.eticaretAPI.entity.Product;
import com.eticare.eticaretAPI.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    ResultData<List<ProductResponse>> getAllProducts(){
        List<Product> products=productService.getAllProduct();
        List<ProductResponse> response=products.stream().map(Product->this.modelMapperService.forResponse().map(Product,ProductResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);
    }

    @GetMapping("/{id}")
    ResultData<ProductResponse> getProductById(@PathVariable Long id){
        Optional<Product> product=productService.getProductById(id);

        return product.map(Product->ResultHelper.success(this.modelMapperService.forResponse().map(Product,ProductResponse.class)))
                .orElse(ResultHelper.errorWithData("Veri bulunamadı",null,HttpStatus.NOT_FOUND));
        //Optional ile gelen veriyi kontrol etmek için .get yerine map fonksiyonunu kullanarak ProductResponse sınıfına dönüştürmek temiz ve modern bir yaklaşımdır.
    }


    @PostMapping("/create")
    ResultData<ProductResponse> createProduct(@RequestBody @Valid ProductSaveRequest productSaveRequest){
        Product product = this.modelMapperService.forRequest().map(productSaveRequest,Product.class);
        productService.createOrUpdate(product);
        ProductResponse response = this.modelMapperService.forResponse().map(product,ProductResponse.class);
        return ResultHelper.created(response);
    }
    @PutMapping("/update")
    ResultData<ProductResponse> createProduct(@RequestBody @Valid ProductUpdateRequest productUpdateRequest){
        Product product = this.modelMapperService.forRequest().map(productUpdateRequest,Product.class);
        productService.createOrUpdate(product);
        ProductResponse response = this.modelMapperService.forResponse().map(product,ProductResponse.class);
        return  ResultHelper.success(response);
    }

    @DeleteMapping("/{id}")
    ResultData<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResultHelper.success(null);
    }
}
