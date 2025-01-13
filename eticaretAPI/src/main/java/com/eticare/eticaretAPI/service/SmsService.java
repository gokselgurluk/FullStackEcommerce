package com.eticare.eticaretAPI.service;

public interface SmsService {
     void sendSms(String toPhoneNumber, String verificationCode);
}
