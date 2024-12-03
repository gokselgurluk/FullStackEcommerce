package com.eticare.eticaretAPI.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name="reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId; // Primary Key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // Foreign Key to Product
    private Product product; // Ürünle ilişki

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Foreign Key to User
    private User user; // Kullanıcıyla ilişki

    @Column(nullable = false)
    private Integer rating; // Kullanıcı değerlendirmesi (1-5)

    @Column(length = 500)
    private String comment; // Kullanıcı yorumu

    @Column(nullable = false)
    private Boolean isApproved = false; // Yorum onayı (varsayılan olarak false)

}
