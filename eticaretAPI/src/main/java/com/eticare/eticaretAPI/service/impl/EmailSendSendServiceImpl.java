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
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.service.EmailSendService;
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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class EmailSendSendServiceImpl implements EmailSendService {

    private final JavaMailSender javaMailSender;
    private final IUserRepository userRepository;
    private final UserService userService;
    private final ITokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final IEmailSendRepository emailSendRepository;
    private static final String IMAGE_PATH = "C:\\Users\\ASUS\\IdeaProjects\\eticaretAPI\\eticaretAPI\\src\\main\\resources\\images\\logo.png";
    @Value("${verify.code.max_attempts}")
    private int remainingAttempts;
    private static final long EMAIL_EXPIRATION = 1000 * 60 * 3; // 3 dk

    public EmailSendSendServiceImpl(JavaMailSender javaMailSender, IUserRepository userRepository, UserService userService, ITokenRepository tokenRepository, JwtService jwtService, AuthenticationService authenticationService, IEmailSendRepository emailSendRepository) {
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.emailSendRepository = emailSendRepository;
    }



    /*@Override
    public BufferedImage generateCodeImage(String code)throws IOException {
        int width = 0;
        int height = 0;

        // Görselleri yükleyip toplam genişlik ve yükseklik hesapla,
        List<BufferedImage> images = new ArrayList<>();
        for (char digit  : code.toCharArray()) {
            String imagePath = "images/" + digit + ".png";
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(imagePath);
            if (inputStream == null) {
                throw new RuntimeException("Görsel bulunamadı: " + imagePath);
            }
            BufferedImage img = ImageIO.read(inputStream);
            images.add(img);
            width+= img.getWidth();
            height = Math.max(height, img.getHeight());
        }
        // Yeni birleştirilmiş görsel oluştur
        BufferedImage combinedImage = new BufferedImage(width ,height,BufferedImage.TYPE_INT_ARGB);
        Graphics g =combinedImage.getGraphics();
        // Görselleri birleştir
        int currentX = 0;
        for (BufferedImage img : images){
            g.drawImage(img,currentX,0,null);
            currentX+=img.getWidth();

        }
        g.dispose();
        return combinedImage;
    }
    public static void saveImage(BufferedImage image, String outputPath) throws IOException {
        // Hedef dosyayı oluştur
        File outputFile = new File(outputPath);

        // Dosyanın olduğu klasör yoksa oluştur
        File parentDir = outputFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Görseli kaydet
        ImageIO.write(image, "png", outputFile);
    }*/

   /* @Override
    public void createImageAndSendEmail(String email, String code) {
        try {
            // Doğrulama kodu için görsel oluştur
            BufferedImage codeImage = generateCodeImage(code);
            String outputPath = "src/main/resources/output/code.png";
            saveImage(codeImage, outputPath);

            // Görseli e-posta ile gönder
            sendVerificationEmailWithMedia(email, outputPath);
        } catch (IOException e) {
            throw new RuntimeException("Görsel oluşturulurken hata oluştu", e);
        }
    }*/

    @Override
    public boolean sendVerificationEmailWithMedia(Token token) {

        String email = token.getUser().getEmail();
        String activationLink = "http://localhost:5173/activate-account?verifyToken=" + token.getToken();

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
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
                    + "<p style='color: red; font-weight: bold;'>Bu link 2 dakika geçerlidir!</p>"
                    + "<img src='cid:logoImage' alt='Şirketinizin logosu' style='width: 150px; height: auto; margin-top: 10px;' />"
                    + "</div>"
                    + "</body></html>";

            // HTML içeriği e-postaya ekle
            helper.setText(htmlContent, true); // true parametresi HTML içeriği olduğunu belirtir
            // Marka logosunu e-posta ile birlikte gömülü olarak ekle
            helper.addInline("logoImage", new File(IMAGE_PATH));

            // E-posta gönder
            javaMailSender.send(message);
            return true; // Hata olmadıysa true dön

        } catch (MessagingException e) {
            throw new RuntimeException("Email içerigi oluşturulamadı" + e.getMessage());
        }
       }

    @Override
    public void sendResetPasswordEmail(String email) {

        try {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                throw new RuntimeException("Email kayıtlı degil");
            }
            String resetPasswordToken = authenticationService.resetPasswordToken(user.get()).getToken();
            String resetUrl = "http://localhost:5173/reset-password?token=" + resetPasswordToken;
            System.out.printf("reset token :" + resetPasswordToken);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("noreply@e-TicaretHesapSifreSıfırlamaService");
            helper.setTo(email);
            helper.setSubject("Account Reset Password Link");
            // HTML formatında içerik
            String htmlContent = "<html><body style='background-color: #f0f0f0; font-family: Arial, sans-serif; text-align: center; padding: 20px;'>"
                    + "<div style='background-color: #ffffff; padding: 40px; border-radius: 8px; box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1); width: 90%; max-width: 600px; margin: 0 auto;'>"
                    + "<h2 style='text-align: center;'>Hoş Geldiniz! 🎉</h2>" + "<h3 style='text-align: center;'>Şifrenizi sıfırlamak için şu linke tıklayın:</h3>"
                    + "<p><a href='" + resetUrl + "' "
                    + "style='display: inline-block; background-color: #007BFF; color: white; padding: 12px 20px; "
                    + "border-radius: 5px; text-decoration: none; font-weight: bold;'>"
                    + "Şifrenizi Sıfırlayın"
                    + "</a></p>"
                    + "<br>"
                    + "<p style='color: red; font-weight: bold;'>Bu link 3 dakika geçerlidir!</p>"
                    + "<img src='cid:logoImage' alt='Şirketinizin logosu' style='width: 150px; height: auto; margin-top: 10px;' />"
                    + "</div>"
                    + "</body></html>";

            // HTML içeriği e-postaya ekle
            helper.setText(htmlContent, true); // true parametresi HTML içeriği olduğunu belirtir
            // Marka logosunu e-posta ile birlikte gömülü olarak ekle
            helper.addInline("logoImage", new File(IMAGE_PATH));

            // E-posta gönder
            javaMailSender.send(message);


        } catch (MessagingException e) {
            throw new RuntimeException("Email içerigi oluşturulamadı :" + e.getMessage());
        }

    }

    @Override
    public String sendVerificationEmail(String email, String code) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.get().isActive()) {
            throw new NotFoundException("Bu hesap zaten aktif");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@springMailService");
        message.setTo(email);
        message.setSubject("Account Verification Code");
        message.setText("Your verification code is: " + code + "\nThis code is valid for 2 minutes.");
        javaMailSender.send(message);

        return "Mesaj gönderildi";
    }

    @Transactional
    @Override
    public EmailSend sendVerifyTokenEmail(String email) {

        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Geçersiz e-posta adresi.");
        }
        User user = userService.getUserByMail(email)
                .orElseThrow(() -> new EntityNotFoundException("Doğrulama tokenı için kullanıcı bulunamadı"));


        if (user.isActive()) {
            throw new IllegalStateException("Hesap zaten aktif.");
        }

      /*  if (!sendVerificationEmailWithMedia(token)) {
            log.warn("E-posta gönderimi başarısız: {}", email);
            return null;
        }*/
        Token token = authenticationService.activationAccountToken(user);
        try {
            sendVerificationEmailWithMedia(token);

          EmailSend emailSend=  saveOrUpdateEmailSend(user, token, null, token.getTokenType(), SecretTypeEnum.TOKEN);

          return emailSend;
        } catch (Exception e) {
            throw new RuntimeException("E-posta kaydedilirken hata oluştu. : "+ e.getMessage());
        }

    }

    @Override
    public EmailSend saveOrUpdateEmailSend(User user, Token token, Code code, TokenType tokenType, SecretTypeEnum secretTypeEnum) {
        EmailSend emailSend = emailSendRepository.findByUserAndTokenTypeAndSecretTypeEnum(user, tokenType, secretTypeEnum).orElse(null);

        if (emailSend != null) {
            // Aynı gün içinde mi kontrol et
            if (DateUtils.isSameDay(emailSend.getLastSendDate(), new Date())) {
                if (emailSend.getRemainingAttempts() > 0) {
                    emailSend.setRemainingAttempts(emailSend.getRemainingAttempts() - 1); // Önce düş
                    throw new IllegalStateException("Bugün için maksimum e-posta gönderim hakkına ulaşıldı.");
                } else {
                    throw new RuntimeException("Bu işlem için e-posta gönderme hakkınız bitti.");
                }
            } else {
                // Yeni gün, hakkı sıfırla
                emailSend.setLastSendDate(new Date());
                emailSend.setRemainingAttempts(remainingAttempts);
            }

            // Token veya kod süresi kontrolü
            if (emailSend.getEmailExpiryDate().after(new Date())) {
                throw new IllegalStateException("Code veya Token geçerliliğini koruyor.");
            }

            // Token veya kodu güncelle
            emailSend.setSecretTypeEnum(token != null ? (SecretTypeEnum.TOKEN) : (SecretTypeEnum.CODE));
            emailSend.setValue(token != null ? token.getToken() : code.getCode());
            emailSend.setEmailExpiryDate(new Date(System.currentTimeMillis() + EMAIL_EXPIRATION));
        }

        // Eğer daha önce kayıt yoksa, yeni oluştur
        if (emailSend == null) {
            emailSend = EmailSend.builder()
                    .user(user)
                    .secretTypeEnum(secretTypeEnum)
                    .tokenType(tokenType)
                    .value(token != null ? token.getToken() : code.getCode())
                    .remainingAttempts(remainingAttempts)
                    .lastSendDate(new Date())
                    .emailExpiryDate(new Date(System.currentTimeMillis()+EMAIL_EXPIRATION))
                    .token(token)
                    .code(code)
                    .build();
        }

        emailSendRepository.save(emailSend);
        return emailSend;
    }

}
