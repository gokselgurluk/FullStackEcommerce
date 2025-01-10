package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerificationToken;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IVerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;


@Service
public class VerificationTokenServiceImpl implements com.eticare.eticaretAPI.service.VerificationTokenService {

    private final IVerificationTokenRepository verificationTokenRepository;

    public VerificationTokenServiceImpl(ITokenRepository tokenRepository, IVerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;

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
    public VerificationToken createVerificationToken(User user) {
        String code = generateVerificationCode(6);
        VerificationToken token = new VerificationToken();
        token.setCode(code);
        token.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2));
        token.setUser(user);
        verificationTokenRepository.save(token);
        return token;
    }

    @Override
    public Boolean validateVerificationCode(String code, User user) {

        Optional<VerificationToken> tokenOtp = verificationTokenRepository.findByCodeAndUser(code, user);

        if (tokenOtp.isPresent()) {
            // Kodun geçerliliğini kontrol et
            LocalDateTime expiryDate = tokenOtp.get().getCodeExpiryDate();
            return expiryDate.isAfter(LocalDateTime.now());
        }

        throw new NotFoundException("Verification code bulunamadı");
    }
}
