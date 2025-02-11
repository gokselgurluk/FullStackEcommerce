package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.TokenType;

import com.eticare.eticaretAPI.service.TokenService;
import com.eticare.eticaretAPI.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {


    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService ;

    public AuthService(JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserService userService, TokenService tokenService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }


    public void register(User user) {
        // Save User in DB
        String refreshToken = jwtService.generateRefreshToken(user);
        Date expiresRefreshToken = (jwtService.extractClaim(refreshToken, Claims::getExpiration));
        //saveOrUpdateToken(user, accessToken, TokenType.ACCESS,expiresAccessToken);
        tokenService.saveOrUpdateToken(user, refreshToken, TokenType.REFRESH);
        // Token'ı veritabanına kaydet

    }


    // Kullanıcı doğrulama ve token üretme
    public List<Token> authenticate(String email, String password) throws RuntimeException {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

           /* // Kullanıcı hesabının kilitli olup olmadığını kontrol et
            if (!customUserDetails.isAccountNonLocked()) {
                throw new BadCredentialsException("Hesabınız kilitlenmiş. Lütfen bekleyin.");
            }*/
            Optional<User> user = userService.getUserByMail(customUserDetails.getUsername()); // Doğrulanan kullanıcıyı al
            List<Token> tokens = new ArrayList<>();
            tokens.add(tokenService.refreshToken(user.get()));
            tokens.add(tokenService.accessToken(user.get()));

            return tokens;
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            throw new RuntimeException(e.getMessage());
        }catch (LockedException e) {
                throw new RuntimeException("Hesap kilitli");
        } catch (AuthenticationException e) {
            throw new RuntimeException("Kimlik doğrulama hatası :" + e.getMessage());
        }



    }

}