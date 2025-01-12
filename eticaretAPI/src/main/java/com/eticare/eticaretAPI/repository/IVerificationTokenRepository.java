package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IVerificationTokenRepository extends JpaRepository<VerificationToken , Long> {
    Optional<VerificationToken> findByUserAndCode( User user,String code);

    Optional<VerificationToken> findByUser(User user);

    @Query("SELECT v FROM VerificationToken v WHERE v.user =:user AND v.lastSendDate = CURRENT_DATE ")
    Optional<VerificationToken> findTodayTokenByUser (@Param("user")User user);
}
