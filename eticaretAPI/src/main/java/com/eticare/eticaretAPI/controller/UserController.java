package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    public ResponseEntity<User> getByUserId(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id){
                userService.deleteUser(id);
        return  ResponseEntity.noContent().build();
    }
}
