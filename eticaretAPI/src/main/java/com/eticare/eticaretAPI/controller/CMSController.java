package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.ModelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.response.CMSResponse;
import com.eticare.eticaretAPI.entity.CMS;
import com.eticare.eticaretAPI.service.CmsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/CMS")
public class CMSController {
    private final CmsService  cmsService;
    private final IModelMapperService modelMapperService;

    public CMSController(CmsService cmsService, IModelMapperService modelMapperService) {
        this.cmsService = cmsService;
        this.modelMapperService = modelMapperService;
    }

    @GetMapping
    ResponseEntity<List<CMSResponse>> getAllContent(){
        List<CMS> contents =cmsService.getAllContents();
        List< CMSResponse> responses = contents.stream().map(CMS->this.modelMapperService.forResponse().map(CMS,CMSResponse.class)).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    ResponseEntity<CMSResponse> getContentById(@PathVariable Long id){
        CMS cms =cmsService.getContentById(id);
        CMSResponse response =this.modelMapperService.forResponse().map(cms,CMSResponse.class);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/author/{authorId}")
    ResponseEntity<List<CMSResponse>> getContentByAuthorId(@PathVariable Long id){

        List<CMS> cmsList =cmsService.getContentsByAuthorId(id);
        List< CMSResponse> response = cmsList.stream().map(CMS->this.modelMapperService.forResponse().map(CMS,CMSResponse.class)).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    ResponseEntity<CMSResponse> createContent(@RequestBody CMS cms){
        CMS createdCms =cmsService.createOrUpdateContend(cms);
        CMSResponse response =this.modelMapperService.forResponse().map(createdCms,CMSResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<CMSResponse> updateContent(@PathVariable Long id ,@RequestBody CMS cms){
        cms.setId(id);
        CMS cmsUpdate =cmsService.createOrUpdateContend(cms);
        CMSResponse response =this.modelMapperService.forResponse().map(cmsUpdate,CMSResponse.class);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    ResponseEntity<CMS> deleteCMS (@PathVariable Long id){
        cmsService.deleteContent(id);
        return  ResponseEntity.noContent().build();
    }
}
