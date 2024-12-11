package com.eticare.eticaretAPI.config.ModelMapper;

import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

public interface IModelMapperService {
    ModelMapper forRequest();
    ModelMapper forResponse();

}
