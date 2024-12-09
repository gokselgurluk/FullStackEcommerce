package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.entity.CMS;
import com.eticare.eticaretAPI.service.CmsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/CMS")
public class CMSController {
    private final CmsService  cmsService;

    public CMSController(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @GetMapping
    ResponseEntity<List<CMS>> getAllContent(){
        List<CMS> contents =cmsService.getAllContents();
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/{id}")
    ResponseEntity<CMS> getContentById(@PathVariable Long id){
        return ResponseEntity.ok(cmsService.getContentById(id));
    }

    @GetMapping("/author/{authorId}")
    ResponseEntity<List<CMS>> getContentByAuthorId(@PathVariable Long id){
        return ResponseEntity.ok(cmsService.getContentsByAuthorId(id));
    }

    @PostMapping
    ResponseEntity<CMS> createContent(@RequestBody CMS cms){
        CMS createdCms =cmsService.createOrUpdateContend(cms);
        return new ResponseEntity<>(createdCms, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<CMS> updateContent(@PathVariable Long id ,@RequestBody CMS cms){
        cms.setId(id);
        return ResponseEntity.ok(cmsService.createOrUpdateContend(cms));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<CMS> deleteCMS (@PathVariable Long id){
        cmsService.deleteContent(id);
        return  ResponseEntity.noContent().build();
    }
}
