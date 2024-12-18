package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.dto.request.User.UserSaveRequest;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.User;
import jakarta.validation.constraints.Email;

import java.util.List;
import java.util.Map;

public interface UserService {
UserResponse createOrUpdateUser (Object object);
Map<String, Object> getAllUsers();
UserResponse getUserById (Long id);

User getUserByMail(String email);
void deleteUser(Long id);


}
