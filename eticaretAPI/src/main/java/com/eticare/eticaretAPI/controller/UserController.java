package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.ModelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<User>createOrUpdateUser(@RequestBody User user){
        return ResponseEntity.ok(userService.createOrUpdateUser(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getByUserId(@PathVariable Long id){
        User user =userService.getUserById(id);
        return ResponseEntity.ok(this.modelMapperService.forResponse().map(user,UserResponse.class));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id){
                userService.deleteUser(id);
        return  ResponseEntity.noContent().build();
    }
}
