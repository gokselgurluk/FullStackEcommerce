package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ITokenRepository extends JpaRepository<Token , Long> {

    List<Token> findAllRefreshTokensByUserIdAndTokenType(Long userId,TokenType tokenType);
    Optional<Token> findByTokenValue(String token);
    List<Token> findAllByUserId(Long userId);
    Optional<Token> findByUserAndTokenValueAndTokenType(User user, String tokenValue, TokenType tokenType);
    Optional<Token> findByUserAndTokenType(User user, TokenType tokenType);


}
