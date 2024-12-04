package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.Order;
import com.eticare.eticaretAPI.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderRepository extends JpaRepository<Order,Long> {

}
