package com.eticare.eticaretAPI.utils;

import jakarta.servlet.http.HttpServletRequest;
import ua_parser.Client;
import ua_parser.Parser;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceUtils {

    // User-Agent bilgisi döner
    public static Map<String, String> getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        // User-Agent Parser kullanımı
        Parser uaParser = new Parser();
        Client client = uaParser.parse(userAgent);

        // Tarayıcı ve OS bilgileri
        String browser = client.userAgent.family;
        String os = client.os.family;

        // Eğer device bilgisi "Other" veya boş ise OS bilgisini kullan
        String device = client.device.family;
        if (device.equals("Other") || device.isEmpty()) {
            device = os; // Cihaz yerine işletim sistemini kullan
        }

        // Bilgileri Map'e ekle
        Map<String, String> parseSessionInfo = new HashMap<>();
        parseSessionInfo.put("Browser", browser);
        parseSessionInfo.put("OS", os);
        parseSessionInfo.put("Device", device); // Güncellenmiş cihaz bilgisi

        return parseSessionInfo;
    }
}

    // Cihaz bilgisi ayrıştırılır
  /*  public static String parseDeviceInfo(String userAgent){



    }
      *//*  if(userAgent.contains("Windows")){
            return "Windows Device";
        }else if(userAgent.contains("Mac OS")){
            return "Mac Device";
        }else if (userAgent.contains("Linux")) {
            return "Linux Device";
        }else{
            return "Unknown Device";
        }
    }*/


