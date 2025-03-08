package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.entity.BlockedIp;
import com.eticare.eticaretAPI.service.BlockedIpService;
import com.eticare.eticaretAPI.utils.IpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class BlockedIpFilter extends OncePerRequestFilter {
    private final BlockedIpService blockedIpService;

    public BlockedIpFilter(BlockedIpService blockedIpService) {
        this.blockedIpService = blockedIpService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String clientIp = IpUtils.getClientIp(request);

        Optional<BlockedIp> blockedIp = blockedIpService.findByBlockedIp(clientIp);
        if (blockedIp.isPresent() && blockedIp.get().isBlockedIpStatus()) {
            response.setStatus(HttpStatus.LOCKED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = new ObjectMapper().writeValueAsString(
                    ResultHelper.successWithData(
                            "Yöneticiyle İletişime Geçin IP adresiniz bloklu",
                            "IP : " + clientIp + " Blok süresi: " + blockedIp.get().getUnblocked_at(),
                            HttpStatus.LOCKED)
            );

            response.getWriter().write(jsonResponse);
            return; // Burada metottan çıkıyoruz, böylece filtre zinciri devam etmiyor
        }

        filterChain.doFilter(request, response);
    }
}
