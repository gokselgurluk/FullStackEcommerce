package com.eticare.eticaretAPI.controller;


import com.eticare.eticaretAPI.config.ModelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.response.OrderResponse;
import com.eticare.eticaretAPI.entity.Order;
import com.eticare.eticaretAPI.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService  orderService;
private final IModelMapperService modelMapperService;

    public OrderController(OrderService orderService, IModelMapperService modelMapperService) {
        this.orderService = orderService;
        this.modelMapperService = modelMapperService;
    }

    @GetMapping
    ResponseEntity<List<OrderResponse>> getAllOrder(){
        List<Order> orders=orderService.getAllOrders();
        List<OrderResponse> response =orders.stream().map(Order->this.modelMapperService.forResponse().map(Order,OrderResponse.class)).collect(Collectors.toList());
        return  ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id){
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(Order->ResponseEntity.ok(this.modelMapperService.forResponse().map(Order,OrderResponse.class))).orElse(ResponseEntity.notFound().build());

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
