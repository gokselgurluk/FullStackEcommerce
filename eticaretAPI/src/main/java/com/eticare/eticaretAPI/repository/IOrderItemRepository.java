package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.OrderItem;
import com.eticare.eticaretAPI.entity.Payment;
import com.eticare.eticaretAPI.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderItemRepository extends JpaRepository<OrderItem,Long> {

}
