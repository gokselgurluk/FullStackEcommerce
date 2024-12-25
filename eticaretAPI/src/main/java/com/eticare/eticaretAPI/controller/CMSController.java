package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.config.result.Result;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
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
    ResultData<List<CMSResponse>> getAllContent(){
        List<CMS> contents =cmsService.getAllContents();
        List< CMSResponse> response = contents.stream().map(CMS->this.modelMapperService.forResponse().map(CMS,CMSResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);
    }

    @GetMapping("/{id}")
    ResultData<CMSResponse> getContentById(@PathVariable Long id){
        CMS cms =cmsService.getContentById(id);
        CMSResponse response =this.modelMapperService.forResponse().map(cms,CMSResponse.class);
        return ResultHelper.success(response);
    }

    @GetMapping("/author/{authorId}")
    ResultData<List<CMSResponse>> getContentByAuthorId(@PathVariable Long id){
        List<CMS> cmsList =cmsService.getContentsByAuthorId(id);
        List< CMSResponse> response = cmsList.stream().map(CMS->this.modelMapperService.forResponse().map(CMS,CMSResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);
    }

    @PostMapping("/create")
    ResultData<CMSResponse> createContent(@RequestBody @Valid CmsSaveRequest cmsSaveRequest){
        CMS createdCms = this.modelMapperService.forRequest().map(cmsSaveRequest,CMS.class);
        cmsService.createOrUpdateContend(createdCms);
        CMSResponse response =this.modelMapperService.forResponse().map(createdCms,CMSResponse.class);
        return ResultHelper.created(response);
    }

    @PutMapping("/update")
    ResultData<CMSResponse> updateContent(@RequestBody @Valid CmsUpdateRequest cmsUpdateRequest){
        CMS updatedCms = this.modelMapperService.forRequest().map(cmsUpdateRequest,CMS.class);
        cmsService.createOrUpdateContend(updatedCms);
        CMSResponse response =this.modelMapperService.forResponse().map(updatedCms,CMSResponse.class);
        return ResultHelper.success(response);

    }

    @DeleteMapping("/{id}")
    Result deleteCMS (@PathVariable Long id){
        cmsService.deleteContent(id);
        return ResultHelper.Ok();
    }
}
