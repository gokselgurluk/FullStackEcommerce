package com.eticare.eticaretAPI.dto.response;

import com.eticare.eticaretAPI.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;

    private String paymentMethod;

    private PaymentStatus paymentStatus;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

    private Long orderId; // Order ID bilgisi döndürülür

    private UserResponse user;;  // User ID bilgisi döndürülür
}
