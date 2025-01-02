package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ITokenRepository extends JpaRepository<Token , Long> {

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND (t.expired=false OR t.revoked=false )")
    List<Token> findAllValidTokensByUser(Long userId);
    Optional<Token> findByToken(String token);

    List<Token> findAllByUserId(Long userId);

    List<Token> findByUserIdAndTokenType(Long userId, TokenType tokenType);
    Optional<Token> findByUserAndTokenType(User user, TokenType tokenType);
}
