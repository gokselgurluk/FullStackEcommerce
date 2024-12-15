package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.ModelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.response.PaymentResponse;
import com.eticare.eticaretAPI.entity.Payment;
import com.eticare.eticaretAPI.entity.enums.PaymentStatus;
import com.eticare.eticaretAPI.service.PaymentService;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    ResponseEntity<List<PaymentResponse>>getAllPayment (){
        List<Payment> payments= paymentService.getAllPayments();
        List<PaymentResponse> response =payments.stream().map(Payment->this.modelMapperService.forResponse().map(Payment,PaymentResponse.class)).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable PaymentStatus paymentStatus){
       List<Payment> payments = paymentService.getPaymentByStatus(paymentStatus);
        List<PaymentResponse> response =payments.stream().map(Payment->this.modelMapperService.forResponse().map(Payment,PaymentResponse.class)).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id){
        Payment payment =paymentService.getPaymentById(id);
        PaymentResponse response = this.modelMapperService.forResponse().map(payment,PaymentResponse.class);
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/orderId/{id}")
    ResponseEntity <List<PaymentResponse>>getPaymentByOrderId(@PathVariable Long id){
        List<Payment> payment = paymentService.getPaymentByOrderId(id);
        List<PaymentResponse> response =payment.stream().map(Payment->this.modelMapperService.forResponse().map(Payment,PaymentResponse.class)).collect(Collectors.toList());
        return  ResponseEntity.ok(response);

    }
    @GetMapping("/userId/{id}")
    ResponseEntity <List<PaymentResponse>>getPaymentByUserId(@PathVariable Long id){
        List<Payment> payment = paymentService.getPaymentByUserId(id);
        List<PaymentResponse> response =payment.stream().map(Payment->this.modelMapperService.forResponse().map(Payment,PaymentResponse.class)).collect(Collectors.toList());
        return ResponseEntity.ok(response);

    }

    // Create a new payment
    @PostMapping
    ResponseEntity<PaymentResponse> createPayment(@RequestBody Payment payment){
        Payment createdPayment=paymentService.createOrUpdate(payment);
        PaymentResponse response =this.modelMapperService.forResponse().map(createdPayment,PaymentResponse.class);
        return new ResponseEntity<>(response , HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    ResponseEntity<PaymentResponse> updatePayment(@PathVariable Long id ,@RequestBody  Payment payment)
    {
        payment.setId(id);
        Payment updatedPayment =paymentService.createOrUpdate(payment);
        PaymentResponse response =this.modelMapperService.forResponse().map(updatedPayment,PaymentResponse.class);
        return new ResponseEntity<>(response ,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Payment> deletePayment(@PathVariable Long id){
        paymentService.deletePayment(id);
       return ResponseEntity.noContent().build();
    }

}
