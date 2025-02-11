package com.eticare.eticaretAPI.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        // 🔹 Endpoint kontrolü: Sadece "/send-activation-email" endpoint'ine gelen isteği kontrol et
        boolean isResendActivationRequest = request.getRequestURI().contains("/send-activation-email");
        boolean isActivateAccountRequest = request.getRequestURI().contains("/auth/activate-account");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        Boolean isActive = null;
        String email = null;


        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                if (jwtService.isTokenValid(jwt)) {
                    isActive = jwtService.isUserActive(jwt);
                    email = jwtService.extractEmail(jwt);
                    System.out.println("JWT ile çıkarılan hesap aktiflik durumu: " + isActive);
                    // Kullanıcı aktif değilse 423 gönder ve işlemi sonlandır
                    if (!isActive && isResendActivationRequest || !isActive && isActivateAccountRequest) {
                        System.out.println("Deactive bilgisi içeren token ile işlem devam etti.");
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                        System.out.println("Kullanıcı detayları yüklendi: " + userDetails.getUsername());

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                        System.out.println("Authentication ayarlandı: " + userDetails.getUsername());

                    } else if (isActive) {
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                            System.out.println("Kullanıcı detayları yüklendi: " + userDetails.getUsername());

                            if (jwtService.isTokenValid(jwt)) {
                                UsernamePasswordAuthenticationToken authToken =
                                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                                System.out.println("Authentication ayarlandı: " + userDetails.getUsername());
                            } else {
                                System.out.println("JWT geçersiz.");
                            }
                        }
                    } else {
                        response.setStatus(423);
                        response.getWriter().write("Hesap Aktif Degil");
                        return;
                    }
                } else {
                    System.out.println("JWT geçersiz.");
                }

            } else {
                System.out.println("JwtAuthenticationFilter : request.getHeader boş");
            }
        } catch (ExpiredJwtException e) {
            // JWT süresi dolmuşsa, 403 status kodu döner
            response.setStatus(403);
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}