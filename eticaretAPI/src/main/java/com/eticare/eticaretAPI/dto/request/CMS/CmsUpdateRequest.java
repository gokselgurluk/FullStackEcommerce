package com.eticare.eticaretAPI.dto.request.CMS;

import lombok.Data;

@Data
public class CmsUpdateRequest {
    private Long id;             // Güncellenecek CMS'nin ID'si
    private String contentTitle; // Güncellenmiş başlık
    private String contentBody;  // Güncellenmiş içerik
    private Long authorUserId;   // (Opsiyonel) Güncellenmiş yazarın User ID'si
}
