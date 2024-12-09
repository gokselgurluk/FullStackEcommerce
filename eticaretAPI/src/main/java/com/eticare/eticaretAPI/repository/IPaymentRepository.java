package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.Payment;
import com.eticare.eticaretAPI.entity.Product;
import com.eticare.eticaretAPI.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment,Long> {

    List<Payment> findByUserId(Long userId);

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
}
