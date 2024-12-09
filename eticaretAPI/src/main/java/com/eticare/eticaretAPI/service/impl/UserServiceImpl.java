package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.service.UserService;
import jakarta.persistence.GeneratedValue;
import org.springframework.stereotype.Service;

import javax.annotation.processing.Generated;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    private final  IUserRepository userRepository;

    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User createOrUpdateUser(User user) {
        return userRepository.save(user);

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found with id :" +id));
    }

    @Override
    public User getUserByMail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
