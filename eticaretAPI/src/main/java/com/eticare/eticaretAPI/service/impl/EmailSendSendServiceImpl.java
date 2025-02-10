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
import com.eticare.eticaretAPI.repository.IUserService;
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
import java.util.Date;
import java.util.Optional;

@Service
public class EmailSendSendServiceImpl implements EmailSendService {

    private final JavaMailSender javaMailSender;
    private final IUserService userRepository;
    private final UserService userService;
    private final ITokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthService authService;
    private final IEmailSendRepository emailSendRepository;
    private static final String IMAGE_PATH = "C:\\Users\\ASUS\\IdeaProjects\\eticaretAPI\\eticaretAPI\\src\\main\\resources\\images\\logo.png";
    @Value("${verify.code.max_attempts}")
    private int remainingAttempts;
    private static final long EMAIL_EXPIRATION = 1000 * 60 * 2; // 3 dk

    public EmailSendSendServiceImpl(JavaMailSender javaMailSender, IUserService userRepository, UserService userService, ITokenRepository tokenRepository, JwtService jwtService, AuthService authService, IEmailSendRepository emailSendRepository) {
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authService = authService;
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
    public void sendVerificationEmailWithMedia(EmailSend emailSend) {


        try {
            String email = emailSend.getUser().getEmail();
            String keyValue = emailSend.getToken()!=null ? emailSend.getToken().getTokenValue() : emailSend.getCode().getCodeValue();                                                                                 ;
            String activationLink = "http://localhost:5173/activate-account?verifyToken=" + keyValue;

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

        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Geçersiz e-posta adresi.");
        }
        User user = userService.getUserByMail(email)
                .orElseThrow(() -> new EntityNotFoundException("Doğrulama tokenı için kullanıcı bulunamadı"));


        if (user.isActive()) {
            throw new IllegalStateException("Hesap zaten aktif.");
        }

        try {
            Token token = authService.activationAccountToken(user);
            EmailSend emailSend=  saveOrUpdateEmailSend(user, token, null, token.getTokenType(), SecretTypeEnum.TOKEN);
            if(emailSend!=null)
            {
                sendVerificationEmailWithMedia(emailSend);

            }
            return emailSend;
        } catch (Exception e) {
            throw new RuntimeException("E-posta kaydedilirken hata oluştu. : "+ e.getMessage());
        }

    }

    @Override
    public void sendResetPasswordEmailWithMedia(EmailSend emailSend) {

        try {
            String email = emailSend.getUser().getEmail();
            String resetPasswordToken = emailSend.getToken()!=null ? emailSend.getToken().getTokenValue() : emailSend.getCode().getCodeValue();
            String resetUrl = "http://localhost:5173/reset-password?token=" + resetPasswordToken;
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
                    + "<p style='color: red; font-weight: bold;'>Bu link 2 dakika süresince geçerlidir!</p>"
                    + "<img src='cid:logoImage' alt='Şirketinizin logosu' style='width: 150px; height: auto; margin-top: 10px;' />"
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

        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Geçersiz e-posta adresi.");
        }
        User user = userService.getUserByMail(email)
                .orElseThrow(() -> new EntityNotFoundException("Doğrulama tokenı için kullanıcı bulunamadı"));


        if (!user.isActive()) {
            throw new IllegalStateException("Hesap aktif degil.");
        }

        try {
            Token token = authService.resetPasswordToken(user);
            EmailSend emailSend = saveOrUpdateEmailSend(user, token, null, token.getTokenType(), SecretTypeEnum.TOKEN);
            if (emailSend != null) {
                sendResetPasswordEmailWithMedia(emailSend);
            }
            return emailSend;
        } catch (Exception e) {
            throw new RuntimeException("E-posta kaydedilirken hata oluştu. : " + e.getMessage());
        }
    }

    @Override
    public String sendVerificationCodeEmail(String email, String code) {
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





    @Override
    public EmailSend saveOrUpdateEmailSend(User user, Token token, Code code, TokenType tokenType, SecretTypeEnum secretTypeEnum) {
        EmailSend emailSend = emailSendRepository.findByUserAndTokenTypeAndSecretTypeEnum(user, tokenType, secretTypeEnum).orElse(null);

        if (emailSend != null) {
            // Token veya kod süresi kontrolü
            if (emailSend.getEmailExpiryDate().before(new Date())) {
                // Aynı gün içinde mi kontrol et
                if (DateUtils.isSameDay(emailSend.getLastSendDate(), new Date())) {
                    if (emailSend.getRemainingAttempts() > 0) {
                        // Token veya kodu güncelle
                        //emailSend.setSecretTypeEnum(token != null ? (SecretTypeEnum.TOKEN) : (SecretTypeEnum.CODE));

                        emailSend.setRemainingAttempts(emailSend.getRemainingAttempts() - 1); // Önce düş

                    } else {
                        throw new RuntimeException("Bugün için maksimum e-posta gönderim hakkına ulaşıldı.");
                    }
                } else {
                    // Yeni gün, hakkı sıfırla
                    emailSend.setRemainingAttempts(remainingAttempts);

                }
            }else {
                throw new IllegalStateException("Code veya Token geçerliliğini koruyor.");
            }

            emailSend.setLastSendDate(new Date());
            emailSend.setEmailExpiryDate(new Date(System.currentTimeMillis() + EMAIL_EXPIRATION));
            emailSend.setValue(token != null ? token.getTokenValue() : code.getCodeValue());


        }

        // Eğer daha önce kayıt yoksa, yeni oluştur
        if (emailSend == null) {
            emailSend = EmailSend.builder()
                    .user(user)
                    .secretTypeEnum(secretTypeEnum)
                    .tokenType(tokenType)
                    .value(token != null ? token.getTokenValue() : code.getCodeValue())
                    .remainingAttempts(remainingAttempts-1)
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
