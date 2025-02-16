package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.EmailSend;
import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;

import java.util.List;
import java.util.Map;

public interface SessionService {
    void createOrUpdateSession(User user, Token token,String ipAddress, Map<String,String> deviceInfo);
    void terminateSession(Long sessionId);
    List<Session> getActiveSessions(String email);
    Session getSessionByRefreshToken(String refreshToken);
    boolean isSessionValid(String email, String ipAddress, String device);
    void incrementFailedLoginAttempts (String email);
    void verifyOtpAndEnableSession(String email , String ipAddress, String device);
    EmailSend requestOtpIfNeeded(User user ,Token token, String ipAddress, Map<String,String> deviceInfo);
    void terminateAllSessionsForUser(Long userId);
}
