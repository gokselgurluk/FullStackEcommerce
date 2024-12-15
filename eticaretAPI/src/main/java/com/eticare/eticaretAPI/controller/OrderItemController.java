package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.ModelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.response.OrderItemResponse;
import com.eticare.eticaretAPI.dto.response.ProductResponse;
import com.eticare.eticaretAPI.entity.Order;
import com.eticare.eticaretAPI.entity.OrderItem;
import com.eticare.eticaretAPI.service.OrderItemService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService  orderItemService;
    private  final IModelMapperService modelMapperService;


    @Autowired
    public OrderItemController(OrderItemService orderItemService, IModelMapperService modelMapperService) {
        this.orderItemService = orderItemService;

        this.modelMapperService = modelMapperService;

    }

    @GetMapping
    ResponseEntity<List<OrderItemResponse>> getAllOrderItem(){
        List<OrderItem> orderItems=orderItemService.getAllOrderItems();
        List<OrderItemResponse> response = orderItems.stream().map(OrderItem->this.modelMapperService.forResponse().map(OrderItem, OrderItemResponse.class)).collect(Collectors.toList());
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Long id){
        Optional<OrderItem> orderItem  = orderItemService.getOrderItemById(id);
        return orderItem.map(OrderItem->ResponseEntity.ok(this.modelMapperService.forResponse().map(OrderItem, OrderItemResponse.class))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    ResponseEntity<OrderItemResponse> createOrderItem(@RequestBody OrderItem orderItem){
        OrderItem createdOrderItem =orderItemService.createOrUpdate(orderItem);
        OrderItemResponse response =this.modelMapperService.forResponse().map(createdOrderItem, OrderItemResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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
