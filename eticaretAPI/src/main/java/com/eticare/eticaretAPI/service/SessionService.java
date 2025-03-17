package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.EmailSend;
import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SessionService {
    void createOrUpdateSession(User user, Token token,String ipAddress, Map<String,String> deviceInfo);
    void terminateSession(Long sessionId);
    List<Session> getActiveSessions(String email);
    Session getSessionByRefreshToken(String refreshToken);
    boolean isSessionValid(Session session);
    boolean isTokenExpired(Date dateExpiration);
    void incrementFailedLoginAttempts (String email, String ipAddress, String device);
    Optional<Session> findByEmailAndIpAddressAndDeviceInfo(String email , String ipAddress, String device);
    EmailSend requestOtpIfNeeded(User user, String ipAddress, Map<String,String> deviceInfo);
    void terminateAllSessionsForUser(Long userId);
}
