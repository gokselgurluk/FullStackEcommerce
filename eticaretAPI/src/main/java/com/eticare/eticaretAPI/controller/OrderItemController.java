package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.config.result.Result;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.Order.OrderUpdateRequest;
import com.eticare.eticaretAPI.dto.request.OrderItem.OrderItemSaveRequest;
import com.eticare.eticaretAPI.dto.response.OrderItemResponse;
import com.eticare.eticaretAPI.entity.OrderItem;
import com.eticare.eticaretAPI.service.OrderItemService;
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
    ResultData<List<OrderItemResponse>> getAllOrderItem(){
        List<OrderItem> orderItems=orderItemService.getAllOrderItems();
        List<OrderItemResponse> response = orderItems.stream().map(OrderItem->this.modelMapperService.forResponse().map(OrderItem, OrderItemResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);

    }

    @GetMapping("/{id}")
    ResultData<OrderItemResponse> getOrderItemById(@PathVariable Long id){
        Optional<OrderItem> orderItem  = orderItemService.getOrderItemById(id);
        return orderItem.map(OrderItem->ResultHelper.success(this.modelMapperService.forResponse().map(OrderItem, OrderItemResponse.class)))
                .orElse(ResultHelper.errorWithData("Veri bulunamadı ",null,HttpStatus.NOT_FOUND));
    }

    @PostMapping("/create")
    ResultData<OrderItemResponse> createOrderItem(@RequestBody OrderItemSaveRequest orderItemSaveRequest){
        OrderItem orderItem=this.modelMapperService.forRequest().map(orderItemSaveRequest,OrderItem.class);
        orderItemService.createOrUpdate(orderItem);
        OrderItemResponse response =this.modelMapperService.forResponse().map(orderItem, OrderItemResponse.class);
        return ResultHelper.success(response);
    }

    @PutMapping("/update")
    ResultData<OrderItemResponse> updateOrderItem(@RequestBody OrderUpdateRequest orderUpdateRequest){
       OrderItem orderItemUpdate=this.modelMapperService.forRequest().map(orderUpdateRequest,OrderItem.class);
        orderItemService.createOrUpdate(orderItemUpdate);
        OrderItemResponse response =this.modelMapperService.forResponse().map(orderItemUpdate,OrderItemResponse.class);
        return ResultHelper.success(response);
    }

    @DeleteMapping
    Result deleteOrderItem(@PathVariable Long id){
        orderItemService.deleteOrderItem(id);
        return ResultHelper.Ok();
    }

}
