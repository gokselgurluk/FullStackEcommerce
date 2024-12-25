package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.EmailAlreadyRegisteredException;
import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.request.User.UserSaveRequest;
import com.eticare.eticaretAPI.dto.request.User.UserUpdateRequest;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    private final  IUserRepository userRepository;
    private final IModelMapperService modelMapperService;

    public UserServiceImpl(IUserRepository userRepository, IModelMapperService modelMapperService) {
        this.userRepository = userRepository;
        this.modelMapperService = modelMapperService;
    }
    public UserResponse createUser(@Valid UserSaveRequest userSaveRequest) {
        if (userRepository.existsByEmail(userSaveRequest.getEmail())) {
            throw new EmailAlreadyRegisteredException("Email daha önce kayıtedilmiş");
        }
        User user = this.modelMapperService.forRequest().map(userSaveRequest, User.class);
        return saveUserAndReturnResponse(user);
    }

    public UserResponse updateUser(@Valid UserUpdateRequest userUpdateRequest) {
        User user = this.modelMapperService.forRequest().map(userUpdateRequest, User.class);
        return saveUserAndReturnResponse(user);
    }

    private UserResponse saveUserAndReturnResponse(User user) {
        // Kullanıcı oluşturma veya güncelleme
        User savedUser = userRepository.save(user);
        // User -> UserResponse dönüşümü
        return modelMapperService.forResponse().map(savedUser, UserResponse.class);
    }

    @Override
    public UserResponse createOrUpdateUser(String action, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Request body cannot be null.");
        }

        if ("create".equalsIgnoreCase(action)) {
            UserSaveRequest userSaveRequest = this.modelMapperService.forRequest().map(object, UserSaveRequest.class);
            return createUser(userSaveRequest);
        } else if ("update".equalsIgnoreCase(action)) {
            UserUpdateRequest userUpdateRequest = this.modelMapperService.forRequest().map(object, UserUpdateRequest.class);
            return updateUser(userUpdateRequest);
        } else {
            throw new IllegalArgumentException("Invalid action type");
        }
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
