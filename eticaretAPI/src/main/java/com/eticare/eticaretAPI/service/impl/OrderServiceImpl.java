package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.Order;
import com.eticare.eticaretAPI.repository.IOrderRepository;
import com.eticare.eticaretAPI.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private  final IOrderRepository orderRepository;

    public OrderServiceImpl(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Override
    public Order createOrUpdate(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long Id) {
        return orderRepository.findById(Id);
    }

    @Override
    public List<Order> getOrderByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public void deleteOrder(Long Id) {
        if (orderRepository.existsById(Id)) {
            orderRepository.deleteById(Id);

        }else {
            throw new RuntimeException("Order not found with id: "+Id);
        }
    }
}
