package com.eticare.eticaretAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CMSResponse {

    private String contentTitle;  // Başlık
    private String contentBody;  // İçerik metni
    private Long authorId;  // İçeriği yazan kullanıcının ID'si
    private String authorUsername;  // İçeriği yazan kullanıcının username'i
    private LocalDateTime createdAt;  // Oluşturulma zamanı
    private LocalDateTime updatedAt;  // Güncellenme zamanı
}
