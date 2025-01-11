package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerificationToken;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.repository.IVerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;


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

        for (int i = 0; i < code; i++) {
            int digit = random.nextInt(10);
            stringBuilder.append(digit);
        }
        return stringBuilder.toString();
    }

    @Override
    public VerificationToken createVerificationToken(String email) {
        // Kullanıcıyı e-posta ile al
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found for email: " + email); // Kullanıcı bulunamazsa hata fırlat
        }

        // Kullanıcı için var olan doğrulama token'ını kontrol et
        Optional<VerificationToken> existingToken = verificationTokenRepository.findByUser(userOptional);

        // Yeni doğrulama kodu oluştur
        String code = generateVerificationCode(6);
        VerificationToken token;

        if (existingToken.isPresent()) {
            // Var olan token'ı güncelle
            token = existingToken.get();
            token.setCode(code);
            token.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Yeni geçerlilik süresi
        } else {
            // Yeni token oluştur
            token = new VerificationToken();
            token.setCode(code);
            token.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Geçerlilik süresi
            token.setUser(userOptional.get()); // Kullanıcıyı set et
        }

        // Token'ı veritabanına kaydet
        verificationTokenRepository.save(token);

        return token;
    }
    @Override
    public Boolean validateVerificationCode(User user,String code) {

        Optional<VerificationToken> tokenOtp = verificationTokenRepository.findByUserAndCode(user,code);

        if (tokenOtp.isPresent()) {
            // Kodun geçerliliğini kontrol et
            LocalDateTime expiryDate = tokenOtp.get().getCodeExpiryDate();
            return expiryDate.isAfter(LocalDateTime.now());
        }

        throw new NotFoundException("Verification code not found for user: " + user.getEmail() + " with code: " + code);
    }
}
