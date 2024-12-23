package com.eticare.eticaretAPI.dto.request.Category;

import lombok.Data;

@Data
public class CategoryUpdateRequest {
    private Long id; // Güncellenecek kategorinin ID'si
    private String name; // Güncellenmiş kategori adı
    private Long parentCategoryId; // (Opsiyonel) Güncellenmiş üst kategori ID'si

}
