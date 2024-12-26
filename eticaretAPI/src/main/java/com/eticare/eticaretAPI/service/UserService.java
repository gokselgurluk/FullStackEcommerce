package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.User;

import java.util.Map;
import java.util.Optional;

public interface UserService {
UserResponse createOrUpdateUser (String action ,Object object);
Map<String, Object> getAllUsers();
UserResponse getUserById (Long id);
Optional<User> findByUsername(String username);
User getUserByMail(String email);
void deleteUser(Long id);


}
