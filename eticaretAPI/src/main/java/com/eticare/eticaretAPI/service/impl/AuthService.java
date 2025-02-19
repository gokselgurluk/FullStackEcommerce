package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.TokenType;

import com.eticare.eticaretAPI.service.TokenService;
import com.eticare.eticaretAPI.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {


    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final UserService userService;
    private final TokenService tokenService;


    public AuthService(JwtService jwtService, AuthenticationManager authenticationManager, UserService userService, TokenService tokenService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;

    }


    public void register(User user) {
        // Save User in DB
        String refreshToken = jwtService.generateRefreshToken(user);
        tokenService.saveOrUpdateToken(user, refreshToken, TokenType.REFRESH);
        // Token'ı veritabanına kaydet

    }


    // Kullanıcı doğrulama ve token üret
    public User authenticate(String email, String password, HttpServletRequest request) throws RuntimeException {
        try {
/*
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
*/
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(email, password);
            authRequest.setDetails(request);
            Authentication authentication = authenticationManager.authenticate(authRequest);
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            return userService.getUserByMail(customUserDetails.getUsername()).orElseThrow(()-> new NotFoundException("authenticate işlemi için kullanıc ıyok")); // Doğrulanan kullanıcıyı al


        } catch (UsernameNotFoundException | BadCredentialsException e) {
            throw new RuntimeException(e.getMessage());
        } catch (LockedException e) {
            throw new LockedException(e.getMessage());
        } catch (AuthenticationException e) {
            throw new RuntimeException("Kimlik doğrulama hatası :" + e.getMessage());
        }


    }

}