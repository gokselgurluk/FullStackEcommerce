package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerifyCode;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.repository.IVerificationTokenRepository;
import com.eticare.eticaretAPI.service.EmailService;
import com.eticare.eticaretAPI.service.VerificationService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static org.apache.commons.lang3.time.DateUtils.isSameDay;


@Service
public class VerificationServiceImpl implements VerificationService {

    private final IVerificationTokenRepository verificationTokenRepository;
    private final IUserRepository userRepository;

    private  final EmailService emailService;

    public VerificationServiceImpl(IVerificationTokenRepository verificationTokenRepository, IUserRepository userRepository, EmailService emailService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    @Override
    public boolean activateUser(String email, String code) {

        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.get().isActive()){
            throw new NotFoundException("Bu hesap zaten aktif");
        }
        User user = userOpt.get();
        if (isValidateCode(user, code)) {
            user.setActive(true); // Hesabı aktif et
            userRepository.save(user);
            return true;
        }
        return false;
    }
    @Override
    public String generateCode(Integer code) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        String charPool = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < code; i++) {
            int index = random.nextInt(charPool.length());
            stringBuilder.append(charPool.charAt(index));
        }
        return stringBuilder.toString();
    }

    @Override
    public VerifyCode createVerifyCode(String email) {
        // Kullanıcıyı e-posta ile al
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new NotFoundException("User not found for email: " + email); // Kullanıcı bulunamazsa hata fırlat
        }

        // Yeni doğrulama kodu oluştur
        String code = generateCode(6);

        // Kullanıcı için var olan doğrulama token'ını kontrol et
        Optional<VerifyCode> existingToken = verificationTokenRepository.findByUser(user.get());
        VerifyCode token;

        if (existingToken.isPresent()) {
            token = existingToken.get();

            // Aynı gün ve maksimum gönderim sınırına ulaşıldıysa hata fırlat
            if (DateUtils.isSameDay(token.getLastSendDate(), new Date())) {
                if (token.getSendCount() >= 3) {
                    throw new IllegalStateException("Bugün için maksimum doğrulama kodu gönderim sınırına ulaşıldı.");
                }
                token.setSendCount(token.getSendCount() + 1);
            } else {
                // Yeni güne geçilmiş, sayaç sıfırlanır
                token.setSendCount(1);
            }

            token.setCode(code);
            token.setLastSendDate(new Date());
            token.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Yeni geçerlilik süresi

        } else {
            // Yeni token oluştur
            token = new VerifyCode();
            token.setUser(user.get()); // Kullanıcıyı set et
            token.setSendCount(1);
            token.setCode(code);
            token.setLastSendDate(new Date());
            token.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Geçerlilik süresi
        }

        // Token'ı veritabanına kaydet
        verificationTokenRepository.save(token);

        return token;
    }

    @Override
    public Boolean isValidateCode(User user, String code) {

        Optional<VerifyCode> tokenOtp = verificationTokenRepository.findByUserAndCode(user, code);

        if (tokenOtp.isPresent()) {
            // Kodun geçerliliğini kontrol et
            LocalDateTime expiryDate = tokenOtp.get().getCodeExpiryDate();
            return expiryDate.isAfter(LocalDateTime.now());
        }

        throw new NotFoundException("Verification code not found for user: " + user.getEmail() + " with code: " + code);
    }

    @Override
    @Transactional
    public VerifyCode sendVerifyCodeAndEmail(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("Authentication failed. User is not logged in.");
        }
        // Yeni bir doğrulama token'ı oluştur
        VerifyCode verifyCode = createVerifyCode(userDetails.getUsername());
        if (verifyCode == null) {
            throw new RuntimeException("Token olsuturulamadı");
        }


        // Doğrulama kodunu e-posta ile gönder
        emailService.sendVerificationEmailWithMedia(userDetails.getUsername(),verifyCode.getCode());

        return verifyCode;
    }
}
