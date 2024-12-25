package com.eticare.eticaretAPI.dto.response;

import com.eticare.eticaretAPI.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    //private long id;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;
    private String shippingAddress;
    private String notes;
    private UserResponse user;
    private PaymentResponse payment;
    private List<OrderItemResponse> orderItemsList;
    private List<PaymentResponse> payments;
}
