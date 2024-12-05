package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrUpdate(Order order);
    List<Order> getAllOrders();

    Optional<Order> getOrderById(Long Id);

    List<Order> getOrderByUserId(Long userId);
    // Belirli bir kullanıcıya ait siparişleri getirir
    void deleteOrder(Long Id);
}
