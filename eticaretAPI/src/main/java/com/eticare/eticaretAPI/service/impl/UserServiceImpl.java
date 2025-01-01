package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.EmailAlreadyRegisteredException;
import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.jwt.AuthenticationService;
import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.request.User.UserSaveRequest;
import com.eticare.eticaretAPI.dto.request.User.UserUpdateRequest;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    private final  IUserRepository userRepository;
    private final IModelMapperService modelMapperService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;


    public UserServiceImpl(IUserRepository userRepository, IModelMapperService modelMapperService, PasswordEncoder passwordEncoder, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.modelMapperService = modelMapperService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
    }
    @Override
    public UserResponse createUser(@Valid UserSaveRequest userSaveRequest) {
        if (userRepository.existsByEmail(userSaveRequest.getEmail())) {
            throw new EmailAlreadyRegisteredException("Email daha önce kayıtedilmiş");
        }
        User user = this.modelMapperService.forRequest().map(userSaveRequest, User.class);
        user.setPassword(passwordEncoder.encode(userSaveRequest.getPassword()));



        return saveUserAndReturnResponse(user);
    }
    @Override
    public UserResponse updateUser(@Valid UserUpdateRequest userUpdateRequest) {
        User user = this.modelMapperService.forRequest().map(userUpdateRequest, User.class);

        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }

        return saveUserAndReturnResponse(user);
    }

    private UserResponse saveUserAndReturnResponse(User user) {
        // Kullanıcı oluşturma veya güncelleme
        User savedUser = userRepository.save(user);
        // User -> UserResponse dönüşümü

        return modelMapperService.forResponse().map(savedUser, UserResponse.class);
    }



    @Override
    public  Map<String, Object>  getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> response =users.stream().map(user->this.modelMapperService.forResponse().map(user,UserResponse.class)).collect(Collectors.toList());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("users", response);
        responseBody.put("total", response.size());
        return responseBody;
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user=userRepository.findById(id).orElseThrow(()->new NotFoundException("User not found with id :" +id));
        return this.modelMapperService.forResponse().map(user,UserResponse.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public User getUserByMail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteUser(Long id) {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
        }else{
          throw new NotFoundException("User not found with id :" +id);
        }

    }
}
