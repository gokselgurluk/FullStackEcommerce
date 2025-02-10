package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.Code;
import com.eticare.eticaretAPI.repository.IUserService;
import com.eticare.eticaretAPI.repository.ICodeRepository;
import com.eticare.eticaretAPI.service.EmailSendService;
import com.eticare.eticaretAPI.service.UserService;
import com.eticare.eticaretAPI.service.CodeService;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static org.apache.commons.lang3.time.DateUtils.isSameDay;


@Service
public class CodeServiceImpl implements CodeService {

    private final ICodeRepository verificationTokenRepository;
    private final IUserService userRepository;
    private final JwtService jwtService;
    private  final EmailSendService emailSendService;
    private  final UserService userService;
    private final AuthService authService;
    @Value("${verify.code.max_attempts}")
    private  int remainingAttempts;
    @Value("${char-pool-set}")
    private  String charPool;
    public CodeServiceImpl(ICodeRepository verificationTokenRepository, IUserService userRepository, JwtService jwtService, EmailSendService emailSendService, UserService userService, AuthService authService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.emailSendService = emailSendService;
        this.userService = userService;
        this.authService = authService;
    }


    @Override
    public boolean activateUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
            user.get().setActive(true); // Hesabı aktif et
            userRepository.save(user.get());
            return true;
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
    public Code createVerifyCode(User user) {

        if (user==null) {
            throw new NotFoundException("Dogrulama kodu oluşturmak için kullanıcı bilgisine ulaşılamadı: " ); // Kullanıcı bulunamazsa hata fırlat
        }

        // Yeni doğrulama kodu oluştur
        String newCode = generateCode(6);

        // Kullanıcı için var olan doğrulama verifyCode'ını kontrol et
        Optional<Code> optionalVerifyCode = verificationTokenRepository.findByUser(user);
        Code code;

        if (optionalVerifyCode.isPresent()) {
            code = optionalVerifyCode.get();
            if(LocalDateTime.now().isAfter(code.getCodeExpiryDate())){
            // Aynı gün ve maksimum gönderim sınırına ulaşıldıysa hata fırlat
                // bu kısma faqraklı bır yoldan maksımum dogrulama sayısı kontrolu yapılabılır mı
            if (DateUtils.isSameDay(code.getLastSendDate(), new Date())) {
                if (code.getRemainingAttempts() <=0) {
                    throw new IllegalStateException("Bugün için maksimum doğrulama kodu oluşturma sınırına ulaşıldı.");
                }
                code.setRemainingAttempts(code.getRemainingAttempts() - 1);
            } else {
                // Yeni güne geçilmiş, sayaç sıfırlanır
                code.setRemainingAttempts(remainingAttempts);
            }
            }else {
                throw new IllegalStateException("Code geçerliligini koruyor");
            }
            code.setCodeValue(newCode);
            code.setLastSendDate(new Date());
            code.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Yeni geçerlilik süresi

        } else {
            // Yeni verifyCode oluştur
            code = new Code();
            code.setUser(user); // Kullanıcıyı set et
            code.setRemainingAttempts(remainingAttempts -1);
            code.setCodeValue(newCode);
            code.setLastSendDate(new Date());
            code.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Geçerlilik süresi

        }

        // onaykodu'u bilgilerini veritabanına kaydet
        verificationTokenRepository.save(code);

        return code;
    }

    @Override
    public Boolean isValidateCode(User user, String code) {


        Optional<Code> tokenOtp = verificationTokenRepository.findByUserAndCodeValue(user, code);

        if (tokenOtp.isPresent()) {
            // Kodun geçerliliğini kontrol et
            LocalDateTime expiryDate = tokenOtp.get().getCodeExpiryDate();
            return expiryDate.isAfter(LocalDateTime.now());
        }

        throw new NotFoundException("Verification code not found for user: " + user.getEmail() + " Gecersiz code: " + code);
    }

    @Override
    public Code sendResetPasswordEmail(String email) {
        return null;
    }


}
