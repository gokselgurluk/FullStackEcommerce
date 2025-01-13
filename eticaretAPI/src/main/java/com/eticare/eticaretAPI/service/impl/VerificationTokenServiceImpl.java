package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerificationToken;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.repository.IVerificationTokenRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import static org.apache.commons.lang3.time.DateUtils.isSameDay;


@Service
public class VerificationTokenServiceImpl implements com.eticare.eticaretAPI.service.VerificationTokenService {

    private final IVerificationTokenRepository verificationTokenRepository;
    private final IUserRepository userRepository;

    public VerificationTokenServiceImpl(ITokenRepository tokenRepository, IVerificationTokenRepository verificationTokenRepository, IUserRepository userRepository) {
        this.verificationTokenRepository = verificationTokenRepository;

        this.userRepository = userRepository;
    }

    @Override
    public String generateVerificationCode(Integer code) {
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
    public VerificationToken createVerificationToken(String email) {
        // Kullanıcıyı e-posta ile al
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new NotFoundException("User not found for email: " + email); // Kullanıcı bulunamazsa hata fırlat
        }

        // Yeni doğrulama kodu oluştur
        String code = generateVerificationCode(6);

        // Kullanıcı için var olan doğrulama token'ını kontrol et
        Optional<VerificationToken> existingToken = verificationTokenRepository.findByUser(user.get());
        VerificationToken token;

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
            token = new VerificationToken();
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
    public Boolean validateVerificationCode(User user, String code) {

        Optional<VerificationToken> tokenOtp = verificationTokenRepository.findByUserAndCode(user, code);

        if (tokenOtp.isPresent()) {
            // Kodun geçerliliğini kontrol et
            LocalDateTime expiryDate = tokenOtp.get().getCodeExpiryDate();
            return expiryDate.isAfter(LocalDateTime.now());
        }

        throw new NotFoundException("Verification code not found for user: " + user.getEmail() + " with code: " + code);
    }
}
