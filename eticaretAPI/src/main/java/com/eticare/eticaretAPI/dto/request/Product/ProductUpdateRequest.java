package com.eticare.eticaretAPI.dto.request.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    @NotNull(message = "Product ID cannot be null.")
    private Long id;

    @NotBlank(message = "Product name cannot be blank.")
    @Size(max = 100, message = "Product name cannot exceed 100 characters.")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;

    @Positive(message = "Price must be positive.")
    private BigDecimal price;

    @Positive(message = "Discount price must be positive.")
    private BigDecimal discountPrice;

    @Positive(message = "Stock quantity must be positive.")
    private Integer stockQuantity;

    private String imageUrl;

    private Long categoryId;
}
