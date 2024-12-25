package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.config.result.Result;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final IModelMapperService modelMapperService;

    public UserController(UserService userService, IModelMapperService modelMapperService) {
        this.userService = userService;
        this.modelMapperService = modelMapperService;
    }

    @PostMapping
    public ResultData<UserResponse> createUser(@RequestParam("action") String action , @RequestBody Object object) {
        UserResponse userResponse =   userService.createOrUpdateUser(action,object);
        return ResultHelper.created(userResponse);
        // UserServise sınıfında user sınıfı maplenıyor metot tıpı  UserResponse donuyor bu yuzden burada maplemedık maplemedık
    }



    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResultHelper.Ok();
    }
}
