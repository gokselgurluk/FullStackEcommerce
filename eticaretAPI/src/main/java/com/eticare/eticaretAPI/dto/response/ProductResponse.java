package com.eticare.eticaretAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    //private Long id; // Ürünün kimliği
    private String name; // Ürün adı
    private String description; // Ürün açıklaması
    private BigDecimal price; // Fiyat
    private BigDecimal discountPrice; // İndirimli fiyat
    private Integer stockQuantity; // Stok miktarı
    private String imageUrl; // Ürün görseli URL'si
    private LocalDateTime createAt; // Oluşturulma zamanı
    private LocalDateTime updateAt; // Güncellenme zamanı

    private String categoryName; // Kategori adı (Category nesnesi yerine doğrudan isim)

    private List<ReviewResponse> reviews; // Yorum listesi
}
