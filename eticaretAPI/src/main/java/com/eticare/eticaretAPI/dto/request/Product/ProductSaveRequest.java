package com.eticare.eticaretAPI.dto.request.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSaveRequest {
    @NotBlank(message = "Product name cannot be blank.")
    @Size(max = 100, message = "Product name cannot exceed 100 characters.")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;

    @NotNull(message = "Price cannot be null.")
    @Positive(message = "Price must be positive.")
    private BigDecimal price;

    @Positive(message = "Discount price must be positive.")
    private BigDecimal discountPrice;

    @NotNull(message = "Stock quantity cannot be null.")
    @Positive(message = "Stock quantity must be positive.")
    private Integer stockQuantity;

    private String imageUrl;

    @NotNull(message = "Category ID cannot be null.")
    private Long categoryId;
}
