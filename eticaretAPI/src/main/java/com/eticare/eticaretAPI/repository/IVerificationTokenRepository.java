package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IVerificationTokenRepository extends JpaRepository<VerificationToken , Long> {
    Optional<VerificationToken> findByCodeAndUser(String code, User user);
}
