package com.eticare.eticaretAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private Long id; // Kategori ID'si
    private String name; // Kategori adı
    private Long parentCategoryId; // Üst kategori ID'si (null olabilir)
    private String parentCategoryName; // Üst kategori adı (null olabilir)
}
