package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.request.CMS.CmsSaveRequest;
import com.eticare.eticaretAPI.dto.request.CMS.CmsUpdateRequest;
import com.eticare.eticaretAPI.dto.response.CMSResponse;
import com.eticare.eticaretAPI.entity.CMS;
import com.eticare.eticaretAPI.service.CmsService;
import jakarta.validation.Valid;
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

    @PostMapping("/create")
    ResponseEntity<CMSResponse> createContent(@RequestBody @Valid CmsSaveRequest cmsSaveRequest){
        CMS createdCms = this.modelMapperService.forRequest().map(cmsSaveRequest,CMS.class);
        cmsService.createOrUpdateContend(createdCms);
        CMSResponse response =this.modelMapperService.forResponse().map(createdCms,CMSResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    ResponseEntity<CMSResponse> updateContent(@RequestBody @Valid CmsUpdateRequest cmsUpdateRequest){
        CMS updatedCms = this.modelMapperService.forRequest().map(cmsUpdateRequest,CMS.class);
        cmsService.createOrUpdateContend(updatedCms);
        CMSResponse response =this.modelMapperService.forResponse().map(updatedCms,CMSResponse.class);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    ResponseEntity<CMS> deleteCMS (@PathVariable Long id){
        cmsService.deleteContent(id);
        return  ResponseEntity.noContent().build();
    }
}
