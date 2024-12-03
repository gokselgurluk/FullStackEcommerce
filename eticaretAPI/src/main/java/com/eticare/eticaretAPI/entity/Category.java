package com.eticare.eticaretAPI.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name="categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId; // Primary Key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id") // Foreign Key to itself (parent category)
    private Category parentCategory; // Üst kategori (null olabilir)

    @Column(nullable = false, length = 100)
    private String name; // Kategori adı

    @OneToMany(mappedBy = "category")
    private List<Product> products;

}
