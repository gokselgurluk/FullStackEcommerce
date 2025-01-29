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
import org.springframework.cglib.core.Local;
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
            throw new NotFoundException("Kullanıcı email bilgisi bulunamadı: " + email); // Kullanıcı bulunamazsa hata fırlat
        }

        // Yeni doğrulama kodu oluştur
        String newCode = generateCode(6);

        // Kullanıcı için var olan doğrulama verifyCode'ını kontrol et
        Optional<VerifyCode> optionalVerifyCode = verificationTokenRepository.findByUser(user.get());
        VerifyCode verifyCode;

        if (optionalVerifyCode.isPresent()) {
            verifyCode = optionalVerifyCode.get();
            if(LocalDateTime.now().isAfter(verifyCode.getCodeExpiryDate())){
            // Aynı gün ve maksimum gönderim sınırına ulaşıldıysa hata fırlat
            if (DateUtils.isSameDay(verifyCode.getLastSendDate(), new Date())) {
                if (verifyCode.getSendCount() >= 3) {
                    throw new IllegalStateException("Bugün için maksimum doğrulama kodu oluşturma sınırına ulaşıldı.");
                }
                verifyCode.setSendCount(verifyCode.getSendCount() + 1);
            } else {
                // Yeni güne geçilmiş, sayaç sıfırlanır
                verifyCode.setSendCount(1);
            }
            }else {
                throw new IllegalStateException("Code geçerliligini koruyor");
            }

            verifyCode.setCode(newCode);
            verifyCode.setLastSendDate(new Date());
            verifyCode.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Yeni geçerlilik süresi

        } else {
            // Yeni verifyCode oluştur
            verifyCode = new VerifyCode();
            verifyCode.setUser(user.get()); // Kullanıcıyı set et
            verifyCode.setSendCount(1);
            verifyCode.setCode(newCode);
            verifyCode.setLastSendDate(new Date());
            verifyCode.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Geçerlilik süresi
        }

        // Code'u veritabanına kaydet
        verificationTokenRepository.save(verifyCode);

        return verifyCode;
    }

    @Override
    public Boolean isValidateCode(User user, String code) {


        Optional<VerifyCode> tokenOtp = verificationTokenRepository.findByUserAndCode(user, code);

        if (tokenOtp.isPresent()) {
            // Kodun geçerliliğini kontrol et
            LocalDateTime expiryDate = tokenOtp.get().getCodeExpiryDate();
            return expiryDate.isAfter(LocalDateTime.now());
        }

        throw new NotFoundException("Verification code not found for user: " + user.getEmail() + " Gecersiz code: " + code);
    }

    @Override
    @Transactional
    public VerifyCode sendVerifyCodeAndEmail(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalStateException("Authentication hatası. kullanıcı giriş yapmamış.");
        }

        // Yeni bir doğrulama codu'ı oluştur
        VerifyCode verifyCode = createVerifyCode(userDetails.getUsername());

        if (verifyCode == null) {
            throw new IllegalStateException("Doğrulama kodu oluşturulamadı.");
        }

        // Doğrulama kodunu e-posta ile gönder
        emailService.sendVerificationEmailWithMedia(userDetails.getUsername(),verifyCode.getCode());

        return verifyCode;
    }
}
