package com.eticare.eticaretAPI.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {

    // IP adresini döner
    public static  String getClientIp(HttpServletRequest request){
        String ipAddress =request.getHeader("X-Forward-For");
        if(ipAddress==null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)){
            ipAddress=request.getRemoteAddr();
        }
        return ipAddress;
    }
}
