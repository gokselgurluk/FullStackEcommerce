package com.eticare.eticaretAPI.dto.request.Category;

import lombok.Data;

@Data
public class CategorySaveRequest {

    private String name; // Kategori adı
    private Long parentCategoryId; // Üst kategori ID (null olabilir)
}
