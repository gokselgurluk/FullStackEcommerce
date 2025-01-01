package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IUserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

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
        String token = jwtService.generateToken(user.getUsername());

        // Token'ı veritabanına kaydet
        saveUserToken(user, token);

        return token;
    }

    // Kullanıcı doğrulama ve token üretme
    public String authenticate(String username, String password) throws Exception {
        // Kullanıcıyı doğrula
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
       // User'ı DB'den al
        Optional<User> user =userRepository.findByUsername(username);
                List<Token> tokens = tokenRepository.findAllValidTokensByUser(user.get().getId());
        // Eğer geçerli bir token varsa, onu döndür
        if (!tokens.isEmpty()) {
            Token token = tokens.get(0); // İlk token'ı al
            if (!token.isExpired() && !token.isRevoked()) {
                return token.getToken();
            }
        }
        // Eğer geçerli token yoksa, yeni token oluştur ve veritabanına kaydet
        String token = jwtService.generateToken(username);
        saveUserToken(user.get(), token);
        return token;

    }

    private void saveUserToken(User user, String token) {
        Date now = new Date();
        long tokenValidityInMillis = 15 * 60 * 1000; // 15 dakika
        Date expiryDate = new Date(now.getTime() + tokenValidityInMillis);
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .tokenType(TokenType.ACCESS)
                .created_at(now)
                .expires_at(expiryDate)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(newToken);
    }
}