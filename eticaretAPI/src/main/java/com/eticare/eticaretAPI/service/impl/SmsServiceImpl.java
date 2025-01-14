package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.service.SmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    // Twilio'dan alacağınız bilgiler
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "1";


    @Override
    public void sendSms(String toPhoneNumber, String verificationCode) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        try {
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(toPhoneNumber), // Alıcı telefon numarası
                    new com.twilio.type.PhoneNumber(""), // Twilio telefon numarası
                    "Your verification code is: " + verificationCode // Mesaj içeriği
            ).create();

            System.out.println("SMS gönderildi: " + message.getSid());
        } catch (Exception e) {
            throw new RuntimeException("SMS gönderimi sırasında hata oluştu: " + e.getMessage());
        }
    }
}
