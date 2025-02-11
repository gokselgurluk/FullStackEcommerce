package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.ISessionRepository;

import com.eticare.eticaretAPI.service.SessionService;
import com.eticare.eticaretAPI.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SessionServiceImpl implements SessionService {
    private final ISessionRepository sessionRepository;
    private final UserService userService;

    public SessionServiceImpl(ISessionRepository sessionRepository, UserService userService) {
        this.sessionRepository = sessionRepository;
        this.userService = userService;
    }

    @Override
    public Session createSession(User user, Token token,String ipAddress, Map<String,String> deviceInfo) {
        Session session = new Session();
        session.setEmail(user.getEmail());
        session.setRefreshToken(token.getTokenValue());
        session.setIpAddress(ipAddress);
        session.setBrowser(deviceInfo.get("Browser"));
        session.setOs(deviceInfo.get("OS"));
        session.setDevice(deviceInfo.get("Device"));
        session.setCreatedAt(new Date());
        session.setExpiresAt(token.getExpires_at()); //  oturum süresi
        session.setToken(token); // Burada token nesnesini sağlamalısınız
        session.setUser(user); // Burada user nesnesini sağlamalısınız

        return sessionRepository.save(session);

    }

    @Override
    public void terminateSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        sessionRepository.delete(session);
    }

    @Override
    public List<Session> getActiveSessions(Long userId) {
        return sessionRepository.findByUserIdAndExpiresAtAfter(userId, new Date());
    }

    @Override
    public Session getSessionByRefreshToken(String refreshToken) {
        return sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new RuntimeException("Session bulunamadı"));
    }


    @Override
    public boolean isValidSession(String email, String ipAddress, String deviceInfo) {
        // Kullanıcıyı email ile bul
        User user = userService.getUserByMail(email).orElseThrow(() -> new NotFoundException("isValidSession Error : Kullanıcı bulunamadı"));

        // Kullanıcının aktif oturumlarını kontrol et
        List<Session> sessions = sessionRepository.findByUserIdAndExpiresAtAfter(user.getId(), new Date());
        return sessions.stream()
                .anyMatch(session ->
                        session.getIpAddress().equals(ipAddress) &&
                                session.getDevice().equals(deviceInfo));
    }



    @Override
    public void terminateAllSessionsForUser(Long userId) {
        sessionRepository.deleteByUserId(userId);
    }
}
