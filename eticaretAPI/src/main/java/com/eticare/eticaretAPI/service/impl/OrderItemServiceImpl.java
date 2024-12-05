package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.OrderItem;
import com.eticare.eticaretAPI.repository.IOrderItemRepository;
import com.eticare.eticaretAPI.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    private  final IOrderItemRepository orderItemRepository;
    // Constructor-based dependency injection
    public OrderItemServiceImpl(IOrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderItem createOrUpdate(OrderItem orderItem) {
        // Eğer ID null değilse, kaydı günceller. Aksi halde yeni kayıt oluşturur.
        return orderItemRepository.save(orderItem);
    }

    @Override
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    @Override
    public Optional<OrderItem> getOrderItemById(Long Id) {
        return orderItemRepository.findById(Id);
    }

    @Override
    public void deleteOrderItem(Long Id) {
        if (orderItemRepository.existsById(Id)) {
            orderItemRepository.deleteById(Id);
        }else
            throw new RuntimeException("OrderITem not found with Id: "+Id);
    }
}
