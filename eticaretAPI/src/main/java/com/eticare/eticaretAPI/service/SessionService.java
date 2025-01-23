package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SessionService {
    Session createSession(User user, Token token,String ipAddress, Map<String,String> deviceInfo);
    void terminateSession(Long sessionId);
    List<Session> getActiveSessions(Long userId);
    Session getSessionByRefreshToken(String refreshToken);
    boolean isValidSession(String email, String ipAddress, String deviceInfo);

    void terminateAllSessionsForUser(Long userId);
}
