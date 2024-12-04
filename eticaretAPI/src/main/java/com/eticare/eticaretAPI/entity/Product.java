package com.eticare.eticaretAPI.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false ,length = 100)
    private String name ;

    @Column(length = 500)
    private String description;

    @Positive
    @Column(nullable = false ,precision = 10 ,scale = 2)
    private BigDecimal price;

    @Column(precision = 10 ,scale = 2)
    private BigDecimal discountPrice;

    @Positive
    @Column(nullable = false)
    private Integer stockQuantity;


    private String imageUrl;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    private LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
    private List<OrderItem> orderItemList;

    // OneToMany ilişki, bir ürünün birden fazla yorumu olabilir
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
    private List<Review> reviewList;


}
