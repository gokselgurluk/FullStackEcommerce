package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerifyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IVerificationTokenRepository extends JpaRepository<VerifyCode, Long> {
    Optional<VerifyCode> findByUserAndCode(User user, String code);

    Optional<VerifyCode> findByUser(User user);


}
