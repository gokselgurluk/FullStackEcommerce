package com.eticare.eticaretAPI.controller;


import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.config.result.Result;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.Order.OrderCreateRequest;
import com.eticare.eticaretAPI.dto.request.Order.OrderUpdateRequest;
import com.eticare.eticaretAPI.dto.response.OrderResponse;
import com.eticare.eticaretAPI.entity.Order;
import com.eticare.eticaretAPI.service.OrderService;
import jakarta.validation.Valid;
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
    ResultData<List<OrderResponse>> getAllOrder(){
        List<Order> orders=orderService.getAllOrders();
        List<OrderResponse> response =orders.stream().map(Order->this.modelMapperService.forResponse().map(Order,OrderResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);

    }

    @GetMapping("/{id}")
    ResultData<OrderResponse> getOrderById(@PathVariable Long id){
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(Order->ResultHelper.success(this.modelMapperService.forResponse().map(Order,OrderResponse.class)))
                .orElse(ResultHelper.errorWithData("Veri bulunamadı",null,HttpStatus.NOT_FOUND));

    }

    @PostMapping("/create")
    ResultData<OrderResponse> createOrder (@RequestBody @Valid OrderCreateRequest orderCreateRequest){
        Order createdOrder = this.modelMapperService.forRequest().map(orderCreateRequest ,Order.class);
        orderService.createOrUpdate(createdOrder);
        OrderResponse response = this.modelMapperService.forResponse().map(createdOrder,OrderResponse.class);
        return ResultHelper.created(response);
    }
    @PutMapping("/update")
    ResultData<OrderResponse> updateOrder(@RequestBody @Valid OrderUpdateRequest orderUpdateRequest){
        Order updatedOrder =this.modelMapperService.forRequest().map(orderUpdateRequest,Order.class);
        orderService.createOrUpdate(updatedOrder);
        OrderResponse response = this.modelMapperService.forResponse().map(updatedOrder,OrderResponse.class);
        return ResultHelper.success(response);
    }
    @DeleteMapping("/{id}")
    Result deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);
        return ResultHelper.Ok();
    }


}
