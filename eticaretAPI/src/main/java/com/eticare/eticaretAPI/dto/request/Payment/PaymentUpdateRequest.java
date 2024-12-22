package com.eticare.eticaretAPI.dto.request.Payment;

import com.eticare.eticaretAPI.entity.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentUpdateRequest {

    @NotNull(message = "Payment method cannot be null.")
    private String paymentMethod;

    @NotNull(message = "Payment status cannot be null.")
    private PaymentStatus paymentStatus;

    @NotNull(message = "Amount cannot be null.")
    @Positive(message = "Amount must be positive.")
    private BigDecimal amount;

    @NotNull(message = "Payment date cannot be null.")
    private LocalDateTime paymentDate;

    @NotNull(message = "Order ID cannot be null.")
    private Long orderId;

    @NotNull(message = "User ID cannot be null.")
    private Long userId;
}

