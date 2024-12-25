package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.config.result.Result;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.Payment.PaymentSaveRequest;
import com.eticare.eticaretAPI.dto.request.Payment.PaymentUpdateRequest;
import com.eticare.eticaretAPI.dto.response.PaymentResponse;
import com.eticare.eticaretAPI.entity.Payment;
import com.eticare.eticaretAPI.entity.enums.PaymentStatus;
import com.eticare.eticaretAPI.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final IModelMapperService modelMapperService;

    @Autowired
    public PaymentController(PaymentService paymentService, IModelMapperService modelMapperService) {
        this.paymentService = paymentService;
        this.modelMapperService = modelMapperService;
    }


    @GetMapping
    ResultData<List<PaymentResponse>> getAllPayment (){
        List<Payment> payments= paymentService.getAllPayments();
        List<PaymentResponse> response =payments.stream().map(Payment->this.modelMapperService.forResponse().map(Payment,PaymentResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);
    }

    @GetMapping("/status/{status}")
    ResultData<List<PaymentResponse>> getPaymentsByStatus(@PathVariable PaymentStatus paymentStatus){
       List<Payment> payments = paymentService.getPaymentByStatus(paymentStatus);
        List<PaymentResponse> response =payments.stream().map(Payment->this.modelMapperService.forResponse().map(Payment,PaymentResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);
    }
    @GetMapping("/{id}")
    ResultData<PaymentResponse> getPaymentById(@PathVariable Long id){
        Payment payment =paymentService.getPaymentById(id);
        PaymentResponse response = this.modelMapperService.forResponse().map(payment,PaymentResponse.class);
        return  ResultHelper.success(response);
    }

    @GetMapping("/orderId/{id}")
    ResultData <List<PaymentResponse>>getPaymentByOrderId(@PathVariable Long id){
        List<Payment> payment = paymentService.getPaymentByOrderId(id);
        List<PaymentResponse> response =payment.stream().map(Payment->this.modelMapperService.forResponse().map(Payment,PaymentResponse.class)).collect(Collectors.toList());
        return  ResultHelper.success(response);

    }
    @GetMapping("/userId/{id}")
    ResultData <List<PaymentResponse>>getPaymentByUserId(@PathVariable Long id){
        List<Payment> payment = paymentService.getPaymentByUserId(id);
        List<PaymentResponse> response =payment.stream().map(Payment->this.modelMapperService.forResponse().map(Payment,PaymentResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);

    }

    // Create a new payment
    @PostMapping("/create")
    ResultData<PaymentResponse> createPayment(@RequestBody @Valid PaymentSaveRequest paymentSaveRequest){
        Payment payment = this.modelMapperService.forRequest().map(paymentSaveRequest,Payment.class);
        paymentService.createOrUpdate(payment);
        PaymentResponse response =this.modelMapperService.forResponse().map(payment,PaymentResponse.class);
        return ResultHelper.created(response);
    }
    @PutMapping("/update")
    ResultData<PaymentResponse> updatePayment(@RequestBody @Valid PaymentUpdateRequest paymentUpdateRequest){
        Payment payment  = this.modelMapperService.forResponse().map(paymentUpdateRequest, Payment.class);
        paymentService.createOrUpdate(payment);
        PaymentResponse response =this.modelMapperService.forResponse().map(payment,PaymentResponse.class);
        return ResultHelper.success(response);
    }
    @PutMapping("/{id}")
    ResultData<PaymentResponse> updatePayment(@PathVariable Long id ,@RequestBody  Payment payment){
        payment.setId(id);
        Payment updatedPayment =paymentService.createOrUpdate(payment);
        PaymentResponse response =this.modelMapperService.forResponse().map(updatedPayment,PaymentResponse.class);
        return ResultHelper.success(response);
    }

    @DeleteMapping("/{id}")
    Result deletePayment(@PathVariable Long id){
        paymentService.deletePayment(id);
        return ResultHelper.Ok();
    }

}
