package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerifyCode;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.repository.IVerificationTokenRepository;
import com.eticare.eticaretAPI.service.EmailService;
import com.eticare.eticaretAPI.service.UserService;
import com.eticare.eticaretAPI.service.VerificationService;
import jakarta.transaction.Transactional;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static org.apache.commons.lang3.time.DateUtils.isSameDay;


@Service
public class VerificationServiceImpl implements VerificationService {

    private final IVerificationTokenRepository verificationTokenRepository;
    private final IUserRepository userRepository;
    private final JwtService jwtService;
    private  final EmailService emailService;
    private  final UserService userService;
    @Value("${verify.code.max_attempts}")
    private  int remainingAttempts;
    @Value("${char-pool-set}")
    private  String charPool;
    public VerificationServiceImpl(IVerificationTokenRepository verificationTokenRepository, IUserRepository userRepository, JwtService jwtService, EmailService emailService, UserService userService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.userService = userService;
    }


    @Override
    public boolean activateUser(String email, String code) {

        Optional<User> user = userRepository.findByEmail(email);
       /* if(user.get().isActive()){
            throw new NotFoundException("Bu hesap zaten aktif");
        }*/
        if (isValidateCode(user.get(), code)) {
            user.get().setActive(true); // Hesabı aktif et
            userRepository.save(user.get());
            return true;
        }
        return false;
    }
    @Override
    public String generateCode(Integer code) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
       // String charPool = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < code; i++) {
            int index = random.nextInt(charPool.length());
            stringBuilder.append(charPool.charAt(index));
        }
        return stringBuilder.toString();
    }

    @Override
    public VerifyCode createVerifyCode(User user) {

        if (user==null) {
            throw new NotFoundException("Dogrulama kodu oluşturmak için kullanıcı bilgisine ulaşılamadı: " ); // Kullanıcı bulunamazsa hata fırlat
        }

        // Yeni doğrulama kodu oluştur
        String newCode = generateCode(6);
        String activationToken = jwtService.generateActivationToken(user, newCode);
        // Kullanıcı için var olan doğrulama verifyCode'ını kontrol et
        Optional<VerifyCode> optionalVerifyCode = verificationTokenRepository.findByUser(user);
        VerifyCode verifyCode;

        if (optionalVerifyCode.isPresent()) {
            verifyCode = optionalVerifyCode.get();
            if(LocalDateTime.now().isAfter(verifyCode.getCodeExpiryDate())){
            // Aynı gün ve maksimum gönderim sınırına ulaşıldıysa hata fırlat
                // bu kısma faqraklı bır yoldan maksımum dogrulama sayısı kontrolu yapılabılır mı
            if (DateUtils.isSameDay(verifyCode.getLastSendDate(), new Date())) {
                if (verifyCode.getRemainingAttempts() <=0) {
                    throw new IllegalStateException("Bugün için maksimum doğrulama kodu oluşturma sınırına ulaşıldı.");
                }
                verifyCode.setRemainingAttempts(verifyCode.getRemainingAttempts() - 1);
            } else {
                // Yeni güne geçilmiş, sayaç sıfırlanır
                verifyCode.setRemainingAttempts(remainingAttempts);
            }
            }else {
                throw new IllegalStateException("Code geçerliligini koruyor");
            }

            verifyCode.setCode(newCode);
            verifyCode.setLastSendDate(new Date());
            verifyCode.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Yeni geçerlilik süresi
            verifyCode.setVerifyToken(activationToken);
        } else {
            // Yeni verifyCode oluştur
            verifyCode = new VerifyCode();
            verifyCode.setUser(user); // Kullanıcıyı set et
            verifyCode.setRemainingAttempts(remainingAttempts -1);
            verifyCode.setCode(newCode);
            verifyCode.setLastSendDate(new Date());
            verifyCode.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Geçerlilik süresi
            verifyCode.setVerifyToken(activationToken);
        }

        // onaykodu'u bilgilerini veritabanına kaydet
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
    public VerifyCode sendVerifyCodeAndEmail(CustomUserDetails userDetails) {
        return null;
    }

    @Transactional
    @Override
    public VerifyCode sendVerifyCodeAndEmail(String email){

        if (email.isBlank()) {
            throw new IllegalStateException("Kullanıcı Email için geçersiz giriş yapmış.");
        }

        Optional<User> user = userService.getUserByMail(email);
        if(user.isPresent()) {
            boolean isActivate =user.get().isActive();
            if (isActivate) {
                throw new RuntimeException("Hesap şuan aktif");
            }
        }
        // Yeni bir doğrulama codu'ı oluştur
        VerifyCode verifyCode = createVerifyCode(user.get());

        if (verifyCode == null) {
            throw new IllegalStateException("Doğrulama kodu oluşturulamadı.");
        }

        // Doğrulama kodunu e-posta ile gönder
       emailService.sendVerificationEmailWithMedia(user.get(),verifyCode);

        return verifyCode;
    }
}
