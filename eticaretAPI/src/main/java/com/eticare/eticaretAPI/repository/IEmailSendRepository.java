package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.Code;
import com.eticare.eticaretAPI.entity.EmailSend;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.SecretTypeEnum;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEmailSendRepository extends JpaRepository<EmailSend ,Long> {
    Optional<EmailSend> findByUserAndTokenTypeAndSecretTypeEnum(User user, TokenType tokenType, SecretTypeEnum secretTypeEnum);
}
