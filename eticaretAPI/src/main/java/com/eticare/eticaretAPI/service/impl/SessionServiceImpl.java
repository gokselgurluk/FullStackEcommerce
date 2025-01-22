package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.ISessionRepository;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.service.SessionService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class SessionServiceImpl implements SessionService {
    private final ISessionRepository sessionRepository;
    private final IUserRepository userRepository; // Kullanıcıyı güncellemek için gerekli

    public SessionServiceImpl(ISessionRepository sessionRepository, IUserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Session createSession(User user, Token token,String ipAddress, String deviceInfo) {
        Session session = new Session();
        session.setEmail(user.getEmail());
        session.setRefreshToken(token.getToken());
        session.setIpAddress(ipAddress);
        session.setDeviceInfo(deviceInfo);
        session.setCreatedAt(new Date());
        session.setExpiresAt(token.getExpires_at()); //  oturum süresi
        session.setToken(token); // Burada token nesnesini sağlamalısınız
        session.setUser(user); // Burada token nesnesini sağlamalısınız
        // Session nesnesini konsola yazdırmak
        System.out.println("Created Session: " + session);
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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kullanıcının aktif oturumlarını kontrol et
        List<Session> sessions = sessionRepository.findByUserIdAndExpiresAtAfter(user.getId(), new Date());
        return sessions.stream()
                .anyMatch(session ->
                        session.getIpAddress().equals(ipAddress) &&
                                session.getDeviceInfo().equals(deviceInfo));
    }




    @Override
    public void terminateAllSessionsForUser(Long userId) {
        sessionRepository.deleteByUserId(userId);
    }
}
