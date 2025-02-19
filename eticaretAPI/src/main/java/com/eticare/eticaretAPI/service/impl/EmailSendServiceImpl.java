package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.entity.Code;
import com.eticare.eticaretAPI.entity.EmailSend;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.SecretTypeEnum;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import com.eticare.eticaretAPI.repository.IEmailSendRepository;

import com.eticare.eticaretAPI.service.CodeService;
import com.eticare.eticaretAPI.service.EmailSendService;
import com.eticare.eticaretAPI.service.TokenService;
import com.eticare.eticaretAPI.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.Optional;

@Service
public class EmailSendServiceImpl implements EmailSendService {
    @Value("${max_attempts}")
    private int remainingAttempts;
    private final JavaMailSender javaMailSender;
    private final UserService userService;
    private final TokenService tokenService;
    private final CodeService codeService;
    private final IEmailSendRepository emailSendRepository;
    private static final String IMAGE_PATH = "C:\\Users\\ASUS\\IdeaProjects\\eticaretAPI\\eticaretAPI\\src\\main\\resources\\images\\logo.png";
    private static final long EMAIL_EXPIRATION = 1000 * 60 * 2; // 2 dk

    public EmailSendServiceImpl(JavaMailSender javaMailSender, UserService userService, TokenService tokenService, CodeService codeService, IEmailSendRepository emailSendRepository) {
        this.javaMailSender = javaMailSender;
        this.userService = userService;
        this.tokenService = tokenService;
        this.codeService = codeService;
        this.emailSendRepository = emailSendRepository;
    }

    @Override
    public void sendVerificationEmailWithMedia(EmailSend emailSend) {

        try {
            String email = emailSend.getUser().getEmail();
            String Value = emailSend.getToken() != null ? emailSend.getToken().getTokenValue() : emailSend.getCode().getCodeValue();
            String activationLink = "http://localhost:5173/activate-account?token=" + Value;

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // UTF-8 karakter seti eklendi
            helper.setFrom("noreply@e-TicaretMailDogrulamaService");
            helper.setTo(email);
            helper.setSubject("Account Verification Code");
            // HTML formatında içerik
            String htmlContent = "<html><body style='background-color: #f0f0f0; font-family: Arial, sans-serif; text-align: center; padding: 20px;'>"
                    + "<div style='background-color: #ffffff; padding: 40px; border-radius: 8px; box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1); width: 90%; max-width: 600px; margin: 0 auto;'>"
                    + "<h2 style='text-align: center;'>Hoş Geldiniz! 🎉</h2>" + "<h3 style='text-align: center;'>Hesabınızı aktive etmek için aşağıdaki butona tıklayın:</h3>"
                    + "<p><a href='" + activationLink + "' "
                    + "style='display: inline-block; background-color: #007BFF; color: white; padding: 12px 20px; "
                    + "border-radius: 5px; text-decoration: none; font-weight: bold;'>"
                    + "Hesabınızı Doğrulayın"
                    + "</a></p>"
                    + "<br>"
                    + "<p style='color: red; font-weight: bold;'>Bu link 2 dakika süresince geçerlidir!</p>"
                    + "<img src='cid:logoImage' alt='Şirketinizin logosu' style='width: 150px; height: auto; margin-top: 10px;' />"
                    + "</div>"
                    + "</body></html>";

            // HTML içeriği e-postaya ekle
            helper.setText(htmlContent, true); // true parametresi HTML içeriği olduğunu belirtir
            // Marka logosunu e-posta ile birlikte gömülü olarak ekle
            File logoFile = new File(IMAGE_PATH);
            if (!logoFile.exists()) {
                throw new RuntimeException("Logo resmi bulunamadı !");
            } else {
                helper.addInline("logoImage", logoFile);
            }

            // E-posta gönder
            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Email içerigi oluşturulamadı" + e.getMessage());
        }
    }


    @Transactional
    @Override
    public EmailSend sendVerifyTokenEmail(String email) {
        try {
            if (StringUtils.isBlank(email)) {
                throw new IllegalArgumentException("Geçersiz e-posta adresi.");
            }
            User user = userService.getUserByMail(email)
                    .orElseThrow(() -> new EntityNotFoundException("sendVerifyTokenEmail : kullanıcı bulunamadı"));

            if (user.isActive()) {
                throw new IllegalStateException("Hesap zaten aktif.");
            }

            Token token = tokenService.activationAccountToken(user);
            EmailSend emailSend = saveOrUpdateEmailSend(user, token, null, token.getTokenType(), SecretTypeEnum.TOKEN);

            if (emailSend != null) {
                sendVerificationEmailWithMedia(emailSend);
            }

            return emailSend;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


    }

    @Override
    public void sendResetPasswordEmailWithMedia(EmailSend emailSend) {

        try {
            String email = emailSend.getUser().getEmail();
            String Value = emailSend.getToken() != null ? emailSend.getToken().getTokenValue() : emailSend.getCode().getCodeValue();
            String resetUrl = "http://localhost:5173/reset-password?token=" + Value;
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("noreply@e-TicaretHesapSifreSıfırlamaService");
            helper.setTo(email);
            helper.setSubject("Account Reset Password Link");
            // HTML formatında içerik
            String htmlContent = "<html><body style='background-color: #f0f0f0; font-family: Arial, sans-serif; text-align: center; padding: 20px;'>"
                    + "<div style='background-color: #ffffff; padding: 40px; border-radius: 8px; box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1); width: 90%; max-width: 600px; margin: 0 auto;'>"
                    + "<img src='cid:logoImage' alt='Şirketinizin logosu' style='width: 150px; height: auto; margin-top: 10px;' />"
                    + "<h2 style='text-align: center;'>Hoş Geldiniz! 🎉</h2>" + "<h3 style='text-align: center;'>Şifrenizi sıfırlamak için şu linke tıklayın:</h3>"
                    + "<p><a href='" + resetUrl + "' "
                    + "style='display: inline-block; background-color: #007BFF; color: white; padding: 12px 20px; "
                    + "border-radius: 5px; text-decoration: none; font-weight: bold;'>"
                    + "Şifrenizi Sıfırlayın"
                    + "</a></p>"
                    + "<br>"
                    + "<p style='color: red; font-weight: bold;'>Bu link 2 dakika süresince geçerlidir!</p>"
                    + "</div>"
                    + "</body></html>";

            // HTML içeriği e-postaya ekle
            helper.setText(htmlContent, true); // true parametresi HTML içeriği olduğunu belirtir
            // Marka logosunu e-posta ile birlikte gömülü olarak ekle
            helper.addInline("logoImage", new File(IMAGE_PATH));

            // E-posta gönder
            File logoFile = new File(IMAGE_PATH);
            if (!logoFile.exists()) {
                throw new RuntimeException("Logo resmi bulunamadı !");
            } else {
                helper.addInline("logoImage", logoFile);
            }
            // E-posta gönder
            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Email içerigi oluşturulamadı :" + e.getMessage());
        }

    }

    @Transactional
    @Override
    public EmailSend sendResetPasswordTokenEmail(String email) {
        try {
            if (StringUtils.isBlank(email)) {
                throw new IllegalArgumentException("Geçersiz e-posta adresi.");
            }
            User user = userService.getUserByMail(email)
                    .orElseThrow(() -> new EntityNotFoundException("sendResetPasswordTokenEmail : kullanıcı bulunamadı"));

            if (!user.isActive()) {
                throw new IllegalStateException("Hesap aktif degil.");
            }


            Token token = tokenService.resetPasswordToken(user);
            EmailSend emailSend = saveOrUpdateEmailSend(user, token, null, token.getTokenType(), SecretTypeEnum.TOKEN);
            if (emailSend != null) {
                sendResetPasswordEmailWithMedia(emailSend);
            }
            return emailSend;
        } catch (Exception e) {
            throw new RuntimeException("E-posta gönderim hatası. : " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public EmailSend sendSecurityCodeEmail(String email) {

        try {
            if (StringUtils.isBlank(email)) {
                throw new IllegalArgumentException("Geçersiz e-posta adresi.");
            }
            User user = userService.getUserByMail(email)
                    .orElseThrow(() -> new EntityNotFoundException("sendSecurityCodeEmail : kullanıcı bulunamadı"));

          /*  if (!user.isActive()) {
                throw new IllegalStateException("Hesap aktif degil.");
            }*/

            Code code = codeService.createVerifyCode(user);
            System.out.println("code uretıldı: " + code.getCodeValue());
            EmailSend emailSend = saveOrUpdateEmailSend(user, null, code, code.getTokenType(), SecretTypeEnum.CODE);
            System.out.println("code uretıldı: " + emailSend.toString());
            if (emailSend != null) {
                sendSecurityCodeEmailWithMedia(emailSend);
            }
            return emailSend;
        } catch (Exception e) {
            throw new RuntimeException("E-posta kaydedilirken hata oluştu. : " + e.getMessage());
        }
    }

    @Override
    public void sendSecurityCodeEmailWithMedia(EmailSend emailSend) {
        try {
            String email = emailSend.getUser().getEmail();
            String Value = emailSend.getToken() != null ? emailSend.getToken().getTokenValue() : emailSend.getCode().getCodeValue();
            String verifyUrl = "http://localhost:5173/otp-verify?code=" + Value + "&email=" + email;            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("noreply@e-TicaretHesapSifreSıfırlamaService");
            helper.setTo(email);
            helper.setSubject("OTP Verify Link");
            // HTML formatında içerik

            String htmlContent = "<html><body style='background-color: #f0f0f0; font-family: Arial, sans-serif; text-align: center; padding: 20px;'>"
                    + "<div style='background-color: #ffffff; padding: 40px; border-radius: 8px; box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1); width: 90%; max-width: 600px; margin: 0 auto;'>"
                    + "<img src='cid:logoImage' alt='Şirketinizin logosu' style='width: 75px; height: auto; margin-top: 2px;' />"
                    + "<h2 style='text-align: center;'>OTP Doğrulama</h2>"
                    + "<h3 style='text-align: center;'>Kodunuzu girin:</h3>"
                    + "<p><strong style='font-size: 24px;'>" + Value + "</strong></p>"
                    + "<p style='color: red; font-weight: bold;'>Bu kod 2 dakika geçerlidir!</p>"
                    + "<p><a href='" + verifyUrl + "' "
                    + "style='display: inline-block; background-color: #007BFF; color: white; padding: 10px 20px; "
                    + "border-radius: 5px; text-decoration: none; font-weight: bold;'>"
                    + "OTP GİRİŞİ"
                    + "</a></p>"
                    + "</div>"
                    + "</body></html>";


            // HTML içeriği e-postaya ekle
            helper.setText(htmlContent, true); // true parametresi HTML içeriği olduğunu belirtir
            // Marka logosunu e-posta ile birlikte gömülü olarak ekle
            helper.addInline("logoImage", new File(IMAGE_PATH));

            // E-posta gönder
            File logoFile = new File(IMAGE_PATH);
            if (!logoFile.exists()) {
                throw new RuntimeException("Logo resmi bulunamadı !");
            } else {
                helper.addInline("logoImage", logoFile);
            }
            // E-posta gönder
            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Email içerigi oluşturulamadı :" + e.getMessage());
        }

    }


    @Override
    public EmailSend saveOrUpdateEmailSend(User user, Token token, Code code, TokenType tokenType, SecretTypeEnum secretTypeEnum) {

       try {

        Optional<EmailSend> optionalEmailSend = emailSendRepository.findByUserAndTokenTypeAndSecretTypeEnum(user, tokenType, secretTypeEnum);
        EmailSend emailSend = new EmailSend();
        if (optionalEmailSend.isPresent()) {
            emailSend = optionalEmailSend.get();
            // Token veya kod süresi kontrolü
            if (emailSend.getEmailExpiryDate().before(new Date())) {
                // Aynı gün içinde mi kontrol et
                if (DateUtils.isSameDay(emailSend.getLastSendDate(), new Date())) {
                    if (emailSend.getRemainingAttempts() > 0) {
                        // Token veya kodu güncelle
                        emailSend.setRemainingAttempts(emailSend.getRemainingAttempts() - 1); // Önce düş

                    } else {
                        throw new RuntimeException("Bugün için maksimum e-posta gönderim hakkına ulaşıldı.");
                    }
                } else {
                    // Yeni gün, hakkı sıfırla
                    emailSend.setRemainingAttempts(remainingAttempts);

                }
            } else {
                throw new IllegalStateException("Code veya Token geçerliliğini koruyor.");
            }
            emailSend.setLastSendDate(new Date());
            emailSend.setEmailExpiryDate(new Date(System.currentTimeMillis() + EMAIL_EXPIRATION));
            emailSend.setValue(token != null ? token.getTokenValue() : code.getCodeValue());
        }
        // Eğer daha önce kayıt yoksa, yeni oluştur
        if (optionalEmailSend.isEmpty()) {
            emailSend.setUser(user);
            emailSend.setSecretTypeEnum(secretTypeEnum);
            emailSend.setTokenType(tokenType);
            emailSend.setValue(token != null ? token.getTokenValue() : code.getCodeValue());
            emailSend.setRemainingAttempts(remainingAttempts - 1);
            emailSend.setLastSendDate(new Date());
            emailSend.setEmailExpiryDate(new Date(System.currentTimeMillis() + EMAIL_EXPIRATION));
            emailSend.setToken(token);
            emailSend.setCode(code);


        }
        emailSendRepository.save(emailSend);
        return emailSend;
       }catch (Exception e){
           throw new RuntimeException("email sınıf ıveri tabanı akyıt sorunu : "+e.getMessage());
       }

    }

}

   /*         emailSend = EmailSend.builder()
                    .user(user)
                    .secretTypeEnum(secretTypeEnum)
                    .tokenType(tokenType)
                    .value(token != null ? token.getTokenValue() : code.getCodeValue())
                    .remainingAttempts(remainingAttempts - 1)
                    .lastSendDate(new Date())
                    .emailExpiryDate(new Date(System.currentTimeMillis() + EMAIL_EXPIRATION))
                    .token(token)
                    .code(code)
                    .build();
        }*/
