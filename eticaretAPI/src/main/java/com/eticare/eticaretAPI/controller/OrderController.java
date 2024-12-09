package com.eticare.eticaretAPI.controller;


import com.eticare.eticaretAPI.entity.Order;
import com.eticare.eticaretAPI.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService  orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    ResponseEntity<List<Order>> getAllOrder(){
        return  ResponseEntity.ok(orderService.getAllOrders());

    }

    @GetMapping("/{id}")
    ResponseEntity<Optional<Order>> getOrderById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getOrderById(id));

    }

    @PostMapping
    ResponseEntity<Order> createOrder (@RequestBody Order order){
        Order createdOrder = orderService.createOrUpdate(order);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    ResponseEntity<Order> updateOrder(@PathVariable Long id ,@RequestBody Order order){
        order.setId(id);
        Order updatedOrder =orderService.createOrUpdate(order);
        return ResponseEntity.ok(updatedOrder);
    }
    @DeleteMapping("/{id}")
    ResponseEntity<Order> deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }


}
