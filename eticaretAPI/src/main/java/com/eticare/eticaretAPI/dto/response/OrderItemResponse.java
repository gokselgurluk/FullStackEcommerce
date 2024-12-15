package com.eticare.eticaretAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {
    private Long id;  // OrderItem ID'si
    private Integer quantity;  // Sipariş miktarı
    private OrderResponse order;  // İlgili Order bilgisi (DTO olarak)
    private ProductResponse product;  // İlgili Product bilgisi (DTO olarak)
}
