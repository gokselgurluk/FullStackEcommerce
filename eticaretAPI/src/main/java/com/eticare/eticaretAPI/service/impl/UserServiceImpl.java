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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Value("${MAX.FAILED.ENTER.COUNT}")
    private Integer MAX_FAILED_ENTER_COUNT;

    private final static long ACCOUNT_LOCKED_TIME =1000*60*30;
    private final IUserRepository userRepository;
    private final IModelMapperService modelMapperService;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(IUserRepository userRepository, IModelMapperService modelMapperService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapperService = modelMapperService;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void updateUserLocked(User user) {
        if(user.getAccountLockedTime() != null && user.getAccountLockedTime().after(new Date())){
            user.setAccountLocked(true);
            save(user);
        }


    }

    @Override
    public void diffLockedTime(User user) {
        /*if(user.getAccountLockedTime()== null){
            user.setDiffLockedTime(Math.max(diffTimeMinute, 0));
        }*/
         if (user.getAccountLockedTime() != null && user.getAccountLockedTime().after(new Date())) {
            long diffMillis = user.getAccountLockedTime().getTime() - System.currentTimeMillis();
            long diffTimeMinute = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
            user.setDiffLockedTime(Math.max(diffTimeMinute, 0));
            userRepository.save(user);
        }

    }

    @Override
    public void resetFailedLoginAttempts(User user) {
        user.setIncrementFailedLoginAttempts(0);
        userRepository.save(user);
    }

    @Override
    public void handleFailedLogin(User user) {
        Date now = new Date();
        // Eğer kullanıcı kilitliyse ve kilit süresi dolmamışsa, işlem yapma
        if (user.getAccountLockedTime() != null && user.getAccountLockedTime().after(now)) {
            return; // Kullanıcı hala kilitli, giriş denemelerine izin yok
        }
        // Başarısız giriş sayacını artır
        user.setIncrementFailedLoginAttempts(user.getIncrementFailedLoginAttempts() + 1);
        // Eğer limit aşıldıysa hesabı kilitle
        if (user.getIncrementFailedLoginAttempts() >= MAX_FAILED_ENTER_COUNT) {
            user.setAccountLockedTime(new Date(System.currentTimeMillis() + ACCOUNT_LOCKED_TIME)); // 30 dk kilitle
        }
        userRepository.save(user);
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
    public Map<String, Object> getAllUsers() {
        // Kullanıcıları al
        List<User> users = userRepository.findAll();
        // Her kullanıcı için ilgili token'ları yükle
       /* List<UserResponse> response = users.stream().map(user -> {
            // Kullanıcıya ait token'ları bul
            List<Token> userTokens = tokenService.findAllByUserId(user.getId());
            // Kullanıcıyı maple
            UserResponse userResponse = this.modelMapperService.forResponse().map(user, UserResponse.class);
            // Token'ları UserResponse'a ekle
            List<String> accessTokens = userTokens.stream()
                    .filter(token -> token.getTokenType().equals(TokenType.ACCESS)) // ACCESS token'ları
                    .map(Token::getTokenValue)
                    .collect(Collectors.toList());
            userResponse.setAccessTokens(accessTokens);

            return userResponse;
        }).collect(Collectors.toList());  */

        // Response Body oluştur
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("users", users);
        responseBody.put("total", users.size());

        return responseBody;
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id :" + id));
        return this.modelMapperService.forResponse().map(user, UserResponse.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }


    @Override
    public Optional<User> getUserByMail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException("User not found with id :" + id);
        }

    }
}
