package com.eticare.eticaretAPI.controller;

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

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @GetMapping
    ResponseEntity<List<Payment>>getAllPayment (){
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/status/{status}")
    ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable PaymentStatus paymentStatus){
       List<Payment> payments = paymentService.getPaymentByStatus(paymentStatus);
        return ResponseEntity.ok(payments);
    }
    @GetMapping("/{id}")
    ResponseEntity<Payment> getPaymentById(@PathVariable Long id){
        return  ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/orderId/{id}")
    ResponseEntity <List<Payment>>getPaymentByOrderId(@PathVariable Long id){
        return  ResponseEntity.ok(paymentService.getPaymentByOrderId(id));
            }
    @GetMapping("/userId/{id}")
    ResponseEntity <List<Payment>>getPaymentByUserId(@PathVariable Long id){
        return ResponseEntity.ok(paymentService.getPaymentByUserId(id));

    }

    // Create a new payment
    @PostMapping
    ResponseEntity<Payment> createPayment(@RequestBody Payment payment){
        Payment createdPayment=paymentService.createOrUpdate(payment);
        return new ResponseEntity<>(createdPayment , HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    ResponseEntity<Payment> updatePayment(@PathVariable Long id ,@RequestBody  Payment payment)
    {
        payment.setId(id);
        Payment updatedPayment =paymentService.createOrUpdate(payment);
        return new ResponseEntity<>(updatedPayment ,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Payment> deletePayment(@PathVariable Long id){
        paymentService.deletePayment(id);
       return ResponseEntity.noContent().build();
    }

}
