package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.EmailSend;
import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.ISessionRepository;

import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.service.EmailSendService;
import com.eticare.eticaretAPI.service.SessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SessionServiceImpl implements SessionService {
    private final ISessionRepository sessionRepository;
    private final EmailSendService emailSendService;
    private final ITokenRepository tokenRepository;
    @Value("${MAX.FAILED.ENTER.COUNT}")
    private Integer MAX_FAILED_ENTER_COUNT;

    public SessionServiceImpl(ISessionRepository sessionRepository, EmailSendService emailSendService, ITokenRepository tokenRepository) {
        this.sessionRepository = sessionRepository;
        this.emailSendService = emailSendService;
        this.tokenRepository = tokenRepository;
    }

    public void createOrUpdateSession(User user, Token token, String ipAddress, Map<String, String> deviceInfo) {
        Optional<Session> sessionOptional = sessionRepository.findByEmailAndIpAddressAndDeviceInfo(
                user.getEmail(), ipAddress, deviceInfo.get("Device"));

        Session session = new Session();

        if (sessionOptional.isPresent()) {
            session = sessionOptional.get();
        } else {
            session.setEmail(user.getEmail());
            session.setIpAddress(ipAddress);
            session.setDeviceInfo(deviceInfo.get("Device"));
        }

        session.setBrowser(deviceInfo.get("Browser"));
        session.setOs(deviceInfo.get("OS"));
        session.setCreatedAt(new Date());
        // Eğer token `null` ise, refreshToken değeri "null" yerine boş string ("") olabilir
        session.setRefreshToken(token != null ? token.getTokenValue() : "");
        session.setExpiresAt(token != null ? token.getExpires_at() : null);
        session.setToken(token); // Eğer token null olursa zaten null atanmış olur
        session.setUser(user);

        sessionRepository.save(session);
    }
    @Override
    public void terminateSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        sessionRepository.delete(session);
    }


    @Override
    public Session getSessionByRefreshToken(String refreshToken) {
        return sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Session bulunamadı"));
    }

    @Override
    public void incrementFailedLoginAttempts(String email) {
        List<Session> sessionList = sessionRepository.findByEmail(email);
        for (Session session : sessionList) {
            session.setIncrementFailedAttempts(session.getIncrementFailedAttempts() + 1);
            if (session.getIncrementFailedAttempts() >= MAX_FAILED_ENTER_COUNT) {
                session.setVerifiedSession(false);
            }
            sessionRepository.save(session);
        }
    }

    @Override
    public boolean isSessionValid(String email, String ipAddress, String device) {
        Optional <Session> session =sessionRepository.findByEmailAndIpAddressAndDeviceInfo(email, ipAddress, device);
        if(session.isPresent()){
            boolean verifiedSession = session.get().isVerifiedSession();
            System.out.println( session.get().isVerifiedSession());
            return verifiedSession;
        }
        return false;
       /* return sessionRepository.findByEmailAndIpAddressAndDeviceInfo(email, ipAddress, device)
                .map(Session::isVerifiedSession) // Eğer session varsa, doğrulama durumunu döndür
                .orElse(false); // Eğer session yoksa, false döndür*/
    }

    @Override
    public void verifyOtpAndEnableSession(String email, String ipAddress, String device) {
        Optional<Session> optionalSession = sessionRepository.findByEmailAndIpAddressAndDeviceInfo(email, ipAddress, device);
        if (optionalSession.isPresent()) {
            optionalSession.get().setVerifiedSession(true);// OTP doğrulandıktan sonra oturumu aktif hale getir
            Token refreshToken = optionalSession.get().getToken();
            refreshToken.setRevoked(false);
            tokenRepository.save(refreshToken);
            sessionRepository.save(optionalSession.get());

        }

    }

    @Override
    public EmailSend requestOtpIfNeeded(User user, Token token, String ipAddress, Map<String, String> deviceInfo) {
        String email = user.getEmail();
        createOrUpdateSession(user, token,ipAddress,  deviceInfo);
        return  emailSendService.sendSecurityCodeEmail(email);
//        if (!isSessionValid(email, ipAddress, deviceInfo.get("Device"))) {

//        }


    }

    @Override
    public List<Session> getActiveSessions(String email) {
        List<Session> sessionList = sessionRepository.findByEmail(email);
        return sessionList;
    }

    @Override
    public void terminateAllSessionsForUser(Long userId) {
        sessionRepository.deleteByUserId(userId);
    }
}
