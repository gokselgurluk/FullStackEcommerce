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
    private final TokenServiceImpl tokenService;
    @Value("${MAX.FAILED.ENTER.COUNT}")
    private Integer MAX_FAILED_ENTER_COUNT;

    public SessionServiceImpl(ISessionRepository sessionRepository, EmailSendService emailSendService, ITokenRepository tokenRepository, TokenServiceImpl tokenService) {
        this.sessionRepository = sessionRepository;
        this.emailSendService = emailSendService;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
    }

    public void createOrUpdateSession(User user, Token token, String ipAddress, Map<String, String> deviceInfo) {
        Optional<Session> sessionOptional = findByEmailAndIpAddressAndDeviceInfo(user.getEmail(), ipAddress, deviceInfo.get("Device"));

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
        //session.setRefreshToken(token != null ? token.getTokenValue() : "");
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
        Optional<Token> token = tokenRepository.findByTokenValue(refreshToken);
        return sessionRepository.findByToken(token.get())
                .orElseThrow(() -> new RuntimeException("Session bulunamadı"));
    }

    @Override
    public void incrementFailedLoginAttempts (String email, String ipAddress, String device)
{
        Optional<Session> optionalSession =  findByEmailAndIpAddressAndDeviceInfo(email,ipAddress,device);

        if(optionalSession.isPresent()){
            Session session = optionalSession.get();
                session.setIncrementFailedAttempts(session.getIncrementFailedAttempts() + 1);
            if (session.getIncrementFailedAttempts() >= MAX_FAILED_ENTER_COUNT) {
                terminateSession(session.getId());
                tokenService.delete(tokenRepository.findByTokenValue(optionalSession.get().getToken().getTokenValue()).get());
            }
            sessionRepository.save(session);
        }
    }

    @Override
    public boolean isSessionValid(Session session) {
     return   isTokenExpired(session.getExpiresAt());
       // return optionalSession.map(Session::isVerifiedSession).orElse(false);

    }

    @Override
    public Optional<Session> findByEmailAndIpAddressAndDeviceInfo(String email, String ipAddress, String device) {
        Optional<Session> optionalSession;
        optionalSession = sessionRepository.findByEmailAndIpAddressAndDeviceInfo(email, ipAddress, device);
        return optionalSession;
    }

    @Override
    public EmailSend requestOtpIfNeeded(User user, String ipAddress, Map<String, String> deviceInfo) {
        String email = user.getEmail();
        return  emailSendService.sendSecurityCodeEmail(email);

    }

    @Override
    public List<Session> getActiveSessions(String email) {
        return sessionRepository.findByEmail(email);
    }

    @Override
    public void terminateAllSessionsForUser(Long userId) {
        sessionRepository.deleteByUserId(userId);
    }
    @Override
    public boolean isTokenExpired(Date dateExpiration) {
        return dateExpiration.before(new Date());
    }
}
