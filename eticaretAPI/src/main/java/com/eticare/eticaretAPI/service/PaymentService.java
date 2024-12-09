package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.Payment;
import com.eticare.eticaretAPI.entity.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {
    Payment createOrUpdate(Payment payment);
    List<Payment> getAllPayments ();
    List<Payment> getPaymentByStatus(PaymentStatus paymentStatus);
    Payment getPaymentById(Long id);
    List<Payment>   getPaymentByUserId(Long userId);
    List<Payment>   getPaymentByOrderId(Long orderId);

    void deletePayment(Long Id);

}
