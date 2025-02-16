package com.eticare.eticaretAPI.controller;
import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.service.TokenService;
import com.eticare.eticaretAPI.service.impl.AuthService;
import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.config.result.Result;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.User.UserUpdateRequest;
import com.eticare.eticaretAPI.dto.response.SessionResponse;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.TokenType;

import com.eticare.eticaretAPI.service.SessionService;
import com.eticare.eticaretAPI.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final IModelMapperService modelMapperService;
    private final SessionService sessionService;
    private  final ModelMapper modelMapper ;
   private   final AuthService authService;
private  final TokenService tokenService;
    public UserController(UserService userService, IModelMapperService modelMapperService, SessionService sessionService, ModelMapper modelMapper, AuthService authService, TokenService tokenService) {
        this.userService = userService;
        this.modelMapperService = modelMapperService;
        this.sessionService = sessionService;
        this.modelMapper = modelMapper;

        this.authService = authService;

        this.tokenService = tokenService;
    }

    @GetMapping("/sessionInfo")
    @PreAuthorize("isAuthenticated()")
    public ResultData<?> getUserSessionInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResultHelper.Error500("User details are null. Authentication failed.");
        }
        Optional<User> user = userService.getUserByMail(userDetails.getUsername());
        if (user.isEmpty()) {
            return ResultHelper.Error500("No user found with email: " + userDetails.getUsername());
        }

        Token token = tokenService.findByUserAndTokenType(user.get() ,TokenType.REFRESH).orElseThrow(()->new RuntimeException("UserController:kullanıcıya aıt token bılgısı bulunamadı"));
        List<Session> sessionList = sessionService.getActiveSessions(user.get().getEmail());
        List<SessionResponse> sessionResponsesList =sessionList.stream().map(Session->this.modelMapperService.forResponse().map(Session,SessionResponse.class)).collect(Collectors.toList());
        // return ResponseEntity.ok(new AuthenticationResponse("accessToken", userDetails.getUsername(), userDetails.getAuthorities(),user.isActive())); // Kullanıcı bilgilerini döndür
            return ResultHelper.success(sessionResponsesList);
    }
    // Bu endpoint'e yalnızca giriş yapmış kullanıcılar erişebilir.
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResultData<?> getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResultHelper.Error500("User details are null. Authentication failed.");
        }
        Optional<User> user = userService.getUserByMail(userDetails.getUsername());
        if (user.isEmpty()) {
            return ResultHelper.Error500("No user found with email: " + userDetails.getUsername());
        }
        UserResponse userResponse =this.modelMapperService.forResponse().map(user,UserResponse.class);
        return ResultHelper.success(userResponse);
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
