package com.eticare.eticaretAPI.controller;
import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.config.jwt.AuthenticationService;
import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.config.result.Result;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.User.UserUpdateRequest;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final IModelMapperService modelMapperService;
    private final AuthenticationService authenticationService;
    private  final ModelMapper modelMapper ;
    public UserController(UserService userService, IModelMapperService modelMapperService, AuthenticationService authenticationService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapperService = modelMapperService;
        this.authenticationService = authenticationService;
        this.modelMapper = modelMapper;
    }


    // Bu endpoint'e yalnızca giriş yapmış kullanıcılar erişebilir.
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResultData<String> getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResultHelper.Error500("User details are null. Authentication failed.");
        }
        Optional<User> user = userService.getUserByMail(userDetails.getUsername());
        if (user.isEmpty()) {
            return ResultHelper.Error500("No user found with email: " + userDetails.getUsername());
        }
        return ResultHelper.success("Kullanıcı Profili: " + user.get().getId()+"\n"
                                                                 + user.get().getUsername()+"\n"
                                                                    +user.get().getRoleEnum()+"\n");
    }

    @PostMapping("/update")
    @PreAuthorize("isAuthenticated()") // Sadece giriş yapmış kullanıcılar
    public ResultData<UserResponse> updateUser(@RequestBody UserUpdateRequest request) {
        UserResponse userResponse =  userService.updateUser(request);

        return ResultHelper.success(userResponse);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()") // Sadece giriş yapmış kullanıcılar

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
