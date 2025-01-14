package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmailServiceImpl implements EmailService {

    private  final JavaMailSender javaMailSender;
    private  final IUserRepository userRepository;
    private static final String IMAGE_PATH = "C:/Users/Asus/Desktop/eticaretAPI/src/main/resources/images/";

    public EmailServiceImpl(JavaMailSender javaMailSender, IUserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
    }

    @Override
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
    }

    @Override
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
    }

    @Override
    public void sendVerificationEmailWithMedia(String toEmail, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);

            helper.setFrom("noreply@springMailService");
            helper.setTo(toEmail);
            helper.setSubject("Account Verification Code");
            // HTML formatında içerik
            String htmlContent = "<html><body style='background-color: #f0f0f0; font-family: Arial, sans-serif; text-align: center; padding: 20px;'>"
                    + "<div style='background-color: #ffffff; padding: 40px; border-radius: 8px; box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1); width: 90%; max-width: 600px; margin: 0 auto;'>"
                    + "<h2>Your Verification Code</h2>"
                    + "<h3><b>Code:</b> " + code + "</h3>"
                    + "<p>This code is valid for 2 minutes.</p>"
                    + "<p><a href='https://your-site.com/verify?code=" + code + "' style='color: #007BFF; text-decoration: none;'>Click here to verify your account</a></p>"
                    + "<br>"
                    // Marka logosunu ekleyin
                    + "<img src='cid:logoImage' alt='Brand Logo' style='width: 150px; height: auto;' />"
                    + "</div>"
                    + "</body></html>";

            // HTML içeriği e-postaya ekle
            helper.setText(htmlContent, true); // true parametresi HTML içeriği olduğunu belirtir
            // Marka logosunu e-posta ile birlikte gömülü olarak ekle

            helper.addInline("logoImage", new File("src\\main\\resources\\images\\logo.png"));

            // E-posta gönder
            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("E-posta gönderimi sırasında hata oluştu", e);
        }
    }

    @Override
    public String sendVerificationEmail(String email, String code) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.get().isActive()){
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

}
