package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IUserService;

import com.eticare.eticaretAPI.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final ITokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final IUserService userRepository;
    private  final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthService(ITokenRepository tokenRepository, JwtService jwtService, AuthenticationManager authenticationManager, IUserService userRepository, PasswordEncoder passwordEncoder, UserService userService) {
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }


    public String register(User user) {
        // Save User in DB
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        Date expiresRefreshToken =(jwtService.extractClaim(refreshToken, Claims::getExpiration));
        //saveOrUpdateToken(user, accessToken, TokenType.ACCESS,expiresAccessToken);
        saveOrUpdateToken(user, refreshToken, TokenType.REFRESH);

        return refreshToken;
        // Token'ı veritabanına kaydet

    }


    // Kullanıcı doğrulama ve token üretme
    public List<Token> authenticate(String email, String password) throws RuntimeException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

                // Kullanıcı adını (email) almak
            String detailsUserMail = customUserDetails.getUsername(); // veya customUserDetails.getEmail() gibi bir metot eklediyseniz
            Optional<User> user = userService.getUserByMail(detailsUserMail); // Doğrulanan kullanıcıyı al

            List<Token> tokens = new ArrayList<>();
            tokens.add(refreshToken(detailsUserMail, user.get()));
            tokens.add(accessToken(user.get()));

            return tokens;
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("Bu e-posta ile kayıtlı kullanıcı bulunamadı: ");
        } catch ( BadCredentialsException e) {
           // hatalı gırıslerı yakalıyoruz

            throw new RuntimeException("Şifre yanlış: ");

        }
        catch (AuthenticationException e) {
            throw new RuntimeException("Kimlik doğrulama sırasında bir hata oluştu. HATA:"+e.getMessage());
        }


    }

    public Token refreshToken(String email,User user){
        String refreshToken = jwtService.generateRefreshToken(email);
        Date expiresRefreshToken =(jwtService.extractClaim(refreshToken, Claims::getExpiration));
        return saveOrUpdateToken(user, refreshToken, TokenType.REFRESH);
    }

    public Token accessToken(User user){
        String accessToken = jwtService.generateAccessToken(user);
        Date expiresAccessToken =(jwtService.extractClaim(accessToken, Claims::getExpiration));
        return saveOrUpdateToken(user, accessToken, TokenType.ACCESS);
    }
    public Token activationAccountToken(User user){
        String activationToken = jwtService.generateActivationToken(user);
        Date expiresAccessToken =(jwtService.extractClaim(activationToken, Claims::getExpiration));
        return saveOrUpdateToken(user, activationToken, TokenType.ACTIVATION);
    }

    public Token resetPasswordToken(User user){
        String resetPasswordToken = jwtService.generateResetPasswordToken(user);
        Date expiresAccessToken =(jwtService.extractClaim(resetPasswordToken, Claims::getExpiration));
        return saveOrUpdateToken(user, resetPasswordToken, TokenType.RESET);
    }


    // Token'ların sürelerini kontrol et ve expired alanını güncelle
    public void checkAndUpdateExpiredTokens() {
        List<Token> tokens = tokenRepository.findAll();
        for (Token token : tokens) {
            if (token.getExpires_at().before(new Date()) && !token.isExpired()) {
                token.setExpired(true);  // Token süresi dolmuşsa expired'ı true yap
                tokenRepository.save(token);
            }
        }
    }

    // Revoked token'ları güncelle
    public void revokeToken(String tokenString) {
        Token token = tokenRepository.findByTokenValue(tokenString).orElseThrow(() -> new RuntimeException("Token not found"));
        token.setRevoked(true);  // Token iptal edilmişse revoked'ı true yap
        tokenRepository.save(token);
    }

    private Token saveOrUpdateToken(User user, String token, TokenType tokenType) {

        Optional<Token> existingToken = tokenRepository.findByUserAndTokenType(user, tokenType);

        Token tokenCreateOrUpdate = null;
        if (existingToken.isPresent()) {
            // Mevcut token'ı güncelle
            tokenCreateOrUpdate = existingToken.get();
            tokenCreateOrUpdate.setTokenValue(token);
            tokenCreateOrUpdate.setCreated_at((jwtService.extractClaim(token, Claims::getIssuedAt)));
            tokenCreateOrUpdate.setExpires_at((jwtService.extractClaim(token, Claims::getExpiration)));
            tokenCreateOrUpdate.setRevoked(false); // Varsayılan olarak revoked false
            tokenCreateOrUpdate.setExpired(false); // Varsayılan olarak expired false
            tokenRepository.save(tokenCreateOrUpdate);
        } else {

            tokenCreateOrUpdate = Token.builder()
                    .user(user)
                    .tokenValue(token)
                    .tokenType(tokenType)
                    .created_at(jwtService.extractClaim(token, Claims::getIssuedAt))
                    .expires_at(jwtService.extractClaim(token, Claims::getExpiration))
                    .expired(false)
                    .revoked(false)
                    .build();
            tokenRepository.save(tokenCreateOrUpdate);
        }
        return tokenCreateOrUpdate;
    }
}