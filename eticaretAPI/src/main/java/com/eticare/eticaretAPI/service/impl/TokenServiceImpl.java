package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.service.TokenService;
import com.eticare.eticaretAPI.service.UserService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {
    private final ITokenRepository tokenRepository;
    private final JwtService jwtService;

    public TokenServiceImpl(ITokenRepository tokenRepository, JwtService jwtService) {
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;

    }

    @Override
    public List<Token> findAllByUserId(Long userId) {
        List<Token> tokenList =tokenRepository.findAllByUserId(userId);
        if(tokenList==null){
            throw new NotFoundException("findAllByUserId : Token listesi boş");
        }
      return tokenList;
    }

    @Override
    public Optional<Token> findByUserAndTokenType(User user, TokenType tokenType) {
        return  tokenRepository.findByUserAndTokenType(user,tokenType);
    }

    @Override
    public Token findByTokenValue(String token) {
      Token tokenObject;
        tokenObject = tokenRepository.findByTokenValue(token).orElseThrow(()-> new RuntimeException("findByTokenValue : token bulunamadı"));
        return  tokenObject;
    }

    @Override
    public void delete(Token token) {
        if(tokenRepository.findById(token.getId()).isEmpty()){
            throw  new RuntimeException("Delet için token bulunamadı");
        }
        tokenRepository.delete(token);
    }

    @Override
    public Token refreshToken(User user) {
        String refreshToken = jwtService.generateRefreshToken(user);
        return saveOrUpdateToken(user, refreshToken, TokenType.REFRESH);
    }

    @Override
    public Token accessToken(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        return saveOrUpdateToken(user, accessToken, TokenType.ACCESS);
    }

    @Override
    public Token activationAccountToken(User user) {
        String activationToken = jwtService.generateActivationToken(user);
        return saveOrUpdateToken(user, activationToken, TokenType.ACTIVATION);
    }

    @Override
    public Token resetPasswordToken(User user) {
        String resetPasswordToken = jwtService.generateResetPasswordToken(user);
        return saveOrUpdateToken(user, resetPasswordToken, TokenType.RESET);
    }
    // Token'ların sürelerini kontrol et ve expired alanını güncelle
    @Override
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
    @Override
    public void revokeToken(String tokenString) {
        Token token = tokenRepository.findByTokenValue(tokenString).orElseThrow(() -> new RuntimeException("Token not found"));
        token.setRevoked(true);  // Token iptal edilmişse revoked'ı true yap
        tokenRepository.save(token);
    }

    @Override
    public Token saveOrUpdateToken(User user,String token, TokenType tokenType) {
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
        }
        tokenRepository.save(tokenCreateOrUpdate);
        return tokenCreateOrUpdate;
    }
}

