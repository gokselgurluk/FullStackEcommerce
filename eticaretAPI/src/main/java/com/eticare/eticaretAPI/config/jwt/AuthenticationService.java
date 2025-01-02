package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IUserRepository;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {

    private final ITokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final IUserRepository userRepository;

    public AuthenticationService(ITokenRepository tokenRepository, JwtService jwtService,
                                 AuthenticationManager authenticationManager, IUserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public String register(User user) {
        // Save User in DB

        //String accessToken = jwtService.generateAccessToken(user.getUsername());
        //Date expiresAccessToken =(jwtService.extractClaim(accessToken, Claims::getExpiration));

        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        Date expiresRefreshToken =(jwtService.extractClaim(refreshToken, Claims::getExpiration));

        // saveOrUpdateToken(user, accessToken, TokenType.ACCESS,expiresAccessToken);
        saveOrUpdateToken(user, refreshToken, TokenType.REFRESH,expiresRefreshToken);

        return refreshToken;
        // Token'ı veritabanına kaydet

    }

    // Kullanıcı doğrulama ve token üretme
    public String authenticate(String username, String password) throws Exception {
        // Kullanıcıyı doğrula
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        // User'ı DB'den al
        Optional<User> user = userRepository.findByUsername(username);
        List<Token> tokens = tokenRepository.findAllValidTokensByUser(user.get().getId());
        tokens.forEach(t -> t.setRevoked(true));
        tokenRepository.saveAll(tokens);

        String accessToken = jwtService.generateAccessToken(username);
        Date expiresAccessToken =(jwtService.extractClaim(accessToken, Claims::getExpiration));
        String refreshToken = jwtService.generateRefreshToken(username);
        Date expiresRefreshToken =(jwtService.extractClaim(accessToken, Claims::getExpiration));
        
        saveOrUpdateToken(user.get(), accessToken, TokenType.ACCESS,expiresAccessToken);
        saveOrUpdateToken(user.get(), refreshToken, TokenType.REFRESH,expiresRefreshToken);
        return accessToken;



       /* // Eğer geçerli bir token varsa, onu döndür
        if (!tokens.isEmpty()) {
            Token token = tokens.get(0); // İlk token'ı al
            if (!token.isExpired() && !token.isRevoked()) {
                return token.getToken();
            }
        }
        // Eğer geçerli token yoksa, yeni token oluştur ve veritabanına kaydet
        String token = jwtService.generateRefreshToken(username);
        saveUserToken(user.get(), token, TokenType.ACCESS);
        return token;*/

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
        Token token = tokenRepository.findByToken(tokenString).orElseThrow(() -> new RuntimeException("Token not found"));
        token.setRevoked(true);  // Token iptal edilmişse revoked'ı true yap
        tokenRepository.save(token);
    }

    private void saveOrUpdateToken(User user, String token, TokenType tokenType, Date expiresAt) {

        Optional<Token> existingToken = tokenRepository.findByUserAndTokenType(user, tokenType);

        if (existingToken.isPresent()) {
            // Mevcut token'ı güncelle
            Token tokenToUpdate = existingToken.get();
            tokenToUpdate.setToken(token);
            tokenToUpdate.setExpires_at(expiresAt);
            tokenToUpdate.setRevoked(false); // Varsayılan olarak revoked false
            tokenToUpdate.setExpired(false); // Varsayılan olarak expired false
            tokenRepository.save(tokenToUpdate);
        } else {

            Token newToken = Token.builder()
                    .user(user)
                    .token(token)
                    .tokenType(tokenType)
                    .created_at(new Date())
                    .expires_at(jwtService.extractClaim(token, Claims::getExpiration))
                    .expired(false)
                    .revoked(false)
                    .build();
            tokenRepository.save(newToken);
        }
    }
}