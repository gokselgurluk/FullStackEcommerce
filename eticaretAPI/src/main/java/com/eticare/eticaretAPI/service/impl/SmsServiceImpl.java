package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.service.SmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsServiceImpl implements SmsService {
    // Twilio'dan alacağınız bilgiler
    private static final String ACCOUNT_SID = "";
    private static final String AUTH_TOKEN = "";
    private static final String TWILIO_PHONE_NUMBER = "TWILIO_PHONE_NUMBER";
    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @Override
    public void sendSms(String toPhoneNumber, String verificationCode) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber), // Alıcı telefon numarası
                    new PhoneNumber(TWILIO_PHONE_NUMBER), // Twilio telefon numarası
                    "Your verification code is: " + verificationCode // Mesaj içeriği
            ).create();

            System.out.println("SMS gönderildi: " + message.getSid());
        } catch (Exception e) {
            throw new RuntimeException("SMS gönderimi sırasında hata oluştu: " + e.getMessage());
        }
    }
}
