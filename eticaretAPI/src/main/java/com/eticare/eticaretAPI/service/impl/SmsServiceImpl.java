package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.service.CodeService;
import com.eticare.eticaretAPI.service.SmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    // Twilio'dan alacağınız bilgiler
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "1";
    private final CodeService codeService;

    public SmsServiceImpl(CodeService codeService) {
        this.codeService = codeService;
    }

    @Override
    public void sendSms(String toPhoneNumber) {

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        try {
            // Yeni bir doğrulama token'ı oluştur
            String verificationCode = codeService.generateCode(6);
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(toPhoneNumber), // Alıcı telefon numarası
                    new com.twilio.type.PhoneNumber(""), // Twilio telefon numarası
                    "Your verification code is: " + verificationCode // Mesaj içeriği
            ).create();
        } catch (Exception e) {
            throw new RuntimeException("SMS gönderimi sırasında hata oluştu: " + e.getMessage());
        }
    }
}
