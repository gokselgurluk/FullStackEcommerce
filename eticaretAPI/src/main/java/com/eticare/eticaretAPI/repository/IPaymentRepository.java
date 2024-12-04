package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.Payment;
import com.eticare.eticaretAPI.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment,Long> {

}
