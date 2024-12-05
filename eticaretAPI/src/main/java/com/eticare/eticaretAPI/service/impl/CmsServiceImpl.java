package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.CMS;
import com.eticare.eticaretAPI.repository.ICMSRepository;
import com.eticare.eticaretAPI.service.CmsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CmsServiceImpl implements CmsService {

    private final ICMSRepository cmsRepository;

    public CmsServiceImpl(ICMSRepository cmsRepository) {
        this.cmsRepository = cmsRepository;
    }

    @Override
    public CMS createOrUpdateContend(CMS cms) {

        if (cms.getId() == null) {
            cms.setCreatedAt(LocalDateTime.now());
            return   cmsRepository.save(cms);
        }
        cms.setUpdatedAt(LocalDateTime.now());
        return   cmsRepository.save(cms);
    }

    @Override
    public List<CMS> getAllContents() {
        return cmsRepository.findAll();
    }

    @Override
    public CMS getContentById(Long Id) {
        return cmsRepository.findById(Id).orElseThrow(()->new RuntimeException("Content not found with id: "+Id));
    }

    @Override
    public List<CMS> getContentsByAuthorId(Long authorId) {
        return cmsRepository.findByAuthorId(authorId);
    }

    @Override
    public void deleteContent(Long Id) {
        if (cmsRepository.existsById(Id)) {
            cmsRepository.deleteById(Id);
        }else
            throw new RuntimeException("Content not found with ıd: "+Id);

    }
}
