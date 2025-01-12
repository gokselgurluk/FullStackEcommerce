package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IUserRepository;

import com.eticare.eticaretAPI.service.impl.VerificationTokenServiceImpl;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private  final PasswordEncoder passwordEncoder;

    private  final VerificationTokenServiceImpl verificationTokenService;

    public AuthenticationService(ITokenRepository tokenRepository, JwtService jwtService,
                                 AuthenticationManager authenticationManager, IUserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenServiceImpl verificationTokenService) {
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
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
    public boolean activateUser(String email, String code) {

        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.get().isActive()){
            throw new NotFoundException("Bu hesap zaten aktif");
        }
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (verificationTokenService.validateVerificationCode(user, code)) {
                user.setActive(true); // Hesabı aktif et
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    // Kullanıcı doğrulama ve token üretme
    public String authenticate(String email, String password) throws NotFoundException {
        // Kullanıcıyı doğrula
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            throw new NotFoundException("Kullanıcı adı veya şifre hatalı.");
        } catch (AuthenticationException e) {
            throw new NotFoundException("Kimlik doğrulama sırasında bir hata oluştu.");
        }        // User'ı DB'den al
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kullanıcı bulunamadı "+email);
        }

        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Şifre yanlış");
        }
         /*   List<Token> tokens = tokenRepository.findAllValidTokensByUser(user.get().getId());
            tokens.forEach(t -> t.setRevoked(true));
            tokenRepository.saveAll(tokens);*/

            String accessToken = jwtService.generateAccessToken(email);
            Date expiresAccessToken =(jwtService.extractClaim(accessToken, Claims::getExpiration));
            String refreshToken = jwtService.generateRefreshToken(email);
            Date expiresRefreshToken =(jwtService.extractClaim(accessToken, Claims::getExpiration));

            saveOrUpdateToken(user.get(), accessToken, TokenType.ACCESS);
            saveOrUpdateToken(user.get(), refreshToken, TokenType.REFRESH);
            return accessToken;


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

    private void saveOrUpdateToken(User user, String token, TokenType tokenType) {

        Optional<Token> existingToken = tokenRepository.findByUserAndTokenType(user, tokenType);

        if (existingToken.isPresent()) {
            // Mevcut token'ı güncelle
            Token tokenToUpdate = existingToken.get();
            tokenToUpdate.setToken(token);
            tokenToUpdate.setCreated_at((jwtService.extractClaim(token, Claims::getIssuedAt)));
            tokenToUpdate.setExpires_at((jwtService.extractClaim(token, Claims::getExpiration)));
            tokenToUpdate.setRevoked(false); // Varsayılan olarak revoked false
            tokenToUpdate.setExpired(false); // Varsayılan olarak expired false
            tokenRepository.save(tokenToUpdate);
        } else {

            Token newToken = Token.builder()
                    .user(user)
                    .token(token)
                    .tokenType(tokenType)
                    .created_at(jwtService.extractClaim(token, Claims::getIssuedAt))
                    .expires_at(jwtService.extractClaim(token, Claims::getExpiration))
                    .expired(false)
                    .revoked(false)
                    .build();
            tokenRepository.save(newToken);
        }
    }
}