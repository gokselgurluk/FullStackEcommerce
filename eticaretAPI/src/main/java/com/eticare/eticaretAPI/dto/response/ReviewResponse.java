package com.eticare.eticaretAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
   // private Long reviewId; // Yorumun kimliği
   // private Long productId; // İlgili ürünün kimliği
   // private Long userId; // Yorumu yazan kullanıcının kimliği
    private String userName; // Yorumu yazan kullanıcının ismi (isteğe bağlı)
    private Integer rating; // Değerlendirme puanı (1-5)
    private String comment; // Kullanıcı yorumu
}
