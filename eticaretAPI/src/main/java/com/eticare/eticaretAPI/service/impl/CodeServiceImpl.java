package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.Code;
import com.eticare.eticaretAPI.entity.enums.TokenType;
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
    @Value("${max_attempts}")
    private  Integer remainingAttempts;
    @Value("${char-pool-set}")
    private  String charPool;
    private final ICodeRepository verificationTokenRepository;
    private  final UserService userService;

    public CodeServiceImpl(ICodeRepository verificationTokenRepository,  UserService userService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userService = userService;

    }


    @Override
    public boolean activateUser(String email) {
        User user = userService.getUserByMail(email).orElseThrow(()-> new NotFoundException("activateUser Error : Kullanıcı bulunamadı"));
            user.setActive(true); // Hesabı aktif et
            userService.save(user);
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
        // Kullanıcı için var olan  verifyCode'ını kontrol et
        Optional<Code> optionalVerifyCode = verificationTokenRepository.findByUserAndTokenType(user,TokenType.VERIFICATION);
        System.out.println("Mevcut Code var mı?: " + optionalVerifyCode.isPresent());
        Code code = new Code();
        if (optionalVerifyCode.isPresent()) {
            code = optionalVerifyCode.get();

            // Aynı gün ve maksimum gönderim sınırına ulaşıldıysa hata fırlat
            if(LocalDateTime.now().isBefore(code.getCodeExpiryDate())) {
                throw new IllegalStateException("Code geçerliligini koruyor");
            }
            if (code.getRemainingAttempts() <=0) {
                throw new IllegalStateException("Bugün için maksimum kod oluşturma sınırına ulaşıldı.");
            }
            if (!DateUtils.isSameDay(code.getLastSendDate(), new Date())) {
                // Yeni güne geçilmiş, sayaç sıfırlanır
                code.setRemainingAttempts(remainingAttempts);
            }
            code.setRemainingAttempts(code.getRemainingAttempts() - 1);
            code.setCodeValue(generateCode(6)); // Yeni verifyCode oluştur
            code.setLastSendDate(new Date());
            code.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Yeni geçerlilik süresi
        } else {


             // Kullanıcıyı set et
            code.setUser(user);
            code.setRemainingAttempts(remainingAttempts - 1);
            code.setCodeValue(generateCode(6)); // Yeni verifyCode oluştur
            code.setTokenType(TokenType.VERIFICATION);
            code.setLastSendDate(new Date());
            code.setCodeExpiryDate(LocalDateTime.now().plusMinutes(2)); // Geçerlilik süresi
            code.setExpired(false);
            code.setRevoked(false);
        }

        // onaykodu'u bilgilerini veritabanına kaydet
        verificationTokenRepository.save(code);
        return code;
    }

    @Override
    public Boolean isValidateCode(String email, String code) {
        User user = userService.getUserByMail(email).orElseThrow(()->new NotFoundException("isValidateCode: için kullanıcı bulunamadı !"));
        Optional<Code> codeValueOtp = verificationTokenRepository.findByUserAndCodeValue(user, code);

        if (codeValueOtp.isPresent()) {
            // Kodun geçerliliğini kontrol et
            LocalDateTime expiryDate = codeValueOtp.get().getCodeExpiryDate();
            return expiryDate.isAfter(LocalDateTime.now());
        }

        throw new NotFoundException("Verification code not found for user: " + user.getEmail() + " Gecersiz code: " + code);
    }
/*  if (optionalVerifyCode==null) {
            throw new NotFoundException("Dogrulama kodu oluşturmak için kullanıcı bilgisine ulaşılamadı: " ); // Kullanıcı bulunamazsa hata fırlat
        }*/

}
