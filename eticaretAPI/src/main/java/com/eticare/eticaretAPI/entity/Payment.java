package com.eticare.eticaretAPI.entity;

import com.eticare.eticaretAPI.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name="paymnets")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long Id;


    @Column(nullable = false)
    private String paymentMethod ;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)// Enum'u veritabanında string olarak saklar
    private PaymentStatus paymentStatus;

    @Column(precision = 10,scale = 2,nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id",nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}
