package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.Payment;
import com.eticare.eticaretAPI.entity.enums.PaymentStatus;
import com.eticare.eticaretAPI.repository.IPaymentRepository;
import com.eticare.eticaretAPI.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final IPaymentRepository paymentRepository;

    public PaymentServiceImpl(IPaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment createOrUpdate(Payment payment) {
        // Tüm ödeme kayıtlarını döner
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> getPaymentByStatus(PaymentStatus paymentStatus) {
        return paymentRepository.findByPaymentStatus(paymentStatus);
    }

    @Override
    public Payment getPaymentById(Long id) {
        // ID'ye göre ödemeyi döner, bulunamazsa hata fırlatır
        return paymentRepository.findById(id).orElseThrow(()->new RuntimeException("Payment not found with id "+id));

    }

    @Override
    public List<Payment> getPaymentByUserId(Long userId) {
        // Kullanıcı ID'sine göre ödemeleri döner
        return paymentRepository.findByUserId(userId);
    }

    @Override
    public List<Payment> getPaymentByOrderId(Long orderId) {
        // Sipariş ID'sine göre ödemeleri döner
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public void deletePayment(Long Id) {
        if (paymentRepository.existsById(Id)){
            paymentRepository.deleteById(Id);

        }else {
            throw new RuntimeException("Payment not found with id: "+Id);
        }

    }
}
