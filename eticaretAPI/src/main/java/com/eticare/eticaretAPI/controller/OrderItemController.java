package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.entity.Order;
import com.eticare.eticaretAPI.entity.OrderItem;
import com.eticare.eticaretAPI.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService  orderItemService;


    @Autowired
    public OrderItemController( OrderItemService orderItemService) {
        this.orderItemService = orderItemService;

    }

    @GetMapping
    ResponseEntity<List<OrderItem>> getAllOrderItem(){
        return ResponseEntity.ok(orderItemService.getAllOrderItems());

    }

    @GetMapping("/{id}")
    ResponseEntity<Optional<OrderItem>> getOrderItemById(@PathVariable Long id){

        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    @PostMapping
    ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItem orderItem){
        OrderItem createdOrderItem =orderItemService.createOrUpdate(orderItem);
        return new ResponseEntity<>(createdOrderItem, HttpStatus.CREATED);
    }

    @PutMapping
    ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id , @RequestBody OrderItem orderItem){
        orderItem.setId(id);
        return ResponseEntity.ok(orderItemService.createOrUpdate(orderItem));
    }

    @DeleteMapping
    ResponseEntity<OrderItem> deleteOrderItem(@PathVariable Long id){
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }

}
