package com.eticare.eticaretAPI.dto.request.CMS;

import lombok.Data;

@Data
public class CmsSaveRequest {

    private String contentTitle; // Başlık
    private String contentBody;  // İçerik
    private Long authorUserId;   // Yazarın User ID'si

    // createdAt ve updatedAt backend'de otomatik atanabilir, bu yüzden burada yer almaz.
}
