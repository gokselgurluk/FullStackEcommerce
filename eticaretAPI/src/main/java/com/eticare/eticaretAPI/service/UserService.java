package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.User;
import jakarta.validation.constraints.Email;

import java.util.List;

public interface UserService {
User createOrUpdateUser (User user);
List<User> getAllUsers();
User getUserById (Long id);

User getUserByMail(String email);
void deleteUser(Long id);


}
