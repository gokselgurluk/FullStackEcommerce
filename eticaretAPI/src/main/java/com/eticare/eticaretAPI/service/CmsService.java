package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.CMS;

import java.util.List;

public interface CmsService {
    CMS createOrUpdateContend (CMS cms);
    List<CMS> getAllContents();
    CMS getContentById(Long Id);
    List<CMS> getContentsByAuthorId(Long authorId);

    void  deleteContent(Long Id);

}
