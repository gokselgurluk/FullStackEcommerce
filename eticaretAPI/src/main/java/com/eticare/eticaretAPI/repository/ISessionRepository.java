package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface ISessionRepository extends JpaRepository<Session , Long> {

    List<Session> findByEmail(String email);
    // Belirli bir kullanıcıya ait aktif oturumları getir
    List<Session> findByUserIdAndExpiresAtAfter(Long userId, Date now);
    // Refresh Token üzerinden oturum bul
    Optional<Session> findByRefreshToken(String token);
    Optional<Session> findByEmailAndIpAddressAndDeviceInfo(String email , String ipAddress , String device);
    // Kullanıcının tüm oturumlarını sil
    void deleteByUserId(Long userId);
}
