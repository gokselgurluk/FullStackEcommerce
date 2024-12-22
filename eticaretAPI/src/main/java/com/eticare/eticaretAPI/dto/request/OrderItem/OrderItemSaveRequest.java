package com.eticare.eticaretAPI.dto.request.OrderItem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderItemSaveRequest {
    @NotNull(message = "Quantity cannot be null.")
    @Positive(message = "Quantity must be a positive value.")
    private Integer quantity;

    @NotNull(message = "Order ID cannot be null.")
    private Long orderId;

    @NotNull(message = "Product ID cannot be null.")
    private Long productId;
}
