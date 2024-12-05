package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment createOrUpdate(Payment payment);
    List<Payment> getAllPayments ();
    Payment getPaymentById(Long id);
    List<Payment>   getPaymentByUserId(Long userId);
    List<Payment>   getPaymentByOrderId(Long orderId);

    void deletePayment(Long Id);

}
