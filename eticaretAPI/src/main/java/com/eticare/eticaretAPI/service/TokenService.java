package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TokenService {
    Token findByTokenValue(String token);
    void delete(Token token);
    Optional<Token> findByUserAndTokenValueAndTokenType(User user,String tokenValue, TokenType tokenType);
    List<Token> findAllByUserId(Long userId);
    Token refreshToken(User user);
    Token accessToken(User user);
    Token activationAccountToken(User user);
    Token resetPasswordToken(User user);
    // Token'ların sürelerini kontrol et ve expired alanını güncelle
    void checkAndUpdateExpiredTokens();
    // Revoked token'ları güncelle
    void revokeToken(String tokenString);
    Token saveOrUpdateToken(User user,String token, TokenType tokenType);

}