package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.Code;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ICodeRepository extends JpaRepository<Code, Long> {
    Optional<Code> findByUserAndCodeValue(User user, String code);

    Optional<Code> findByUserAndTokenType(User user, TokenType tokenType);


}
