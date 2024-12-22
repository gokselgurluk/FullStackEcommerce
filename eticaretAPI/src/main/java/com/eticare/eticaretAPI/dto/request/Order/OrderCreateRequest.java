package com.eticare.eticaretAPI.dto.request.Order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class OrderCreateRequest {
    @NotNull(message = "User ID cannot be null.")
    private Long userId;

    @NotNull(message = "Product IDs cannot be null.")
    private List<@NotNull(message = "Product ID cannot be null.") @Positive(message = "Product ID must be positive.") Long> productIds;

    @NotNull(message = "Quantities cannot be null.")
    private List<@NotNull(message = "Quantity cannot be null.") @Positive(message = "Quantity must be positive.") Integer> quantities;

}
