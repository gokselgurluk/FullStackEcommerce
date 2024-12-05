package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemService {
    OrderItem createOrUpdate(OrderItem orderItem);
    List<OrderItem> getAllOrderItems();
    Optional<OrderItem> getOrderItemById(Long Id);
    void deleteOrderItem(Long Id);

}
