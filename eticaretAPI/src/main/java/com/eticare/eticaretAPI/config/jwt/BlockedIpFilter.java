package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.entity.BlockedIp;
import com.eticare.eticaretAPI.service.BlockedIpService;
import com.eticare.eticaretAPI.utils.IpUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
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
        // BlockedIp nesnesini alıp, IP'nin bloklu olup olmadığını kontrol etmek
      Optional<BlockedIp>  blockedIp =blockedIpService.findByBlockedIp(clientIp);
        if(blockedIp.isPresent() && blockedIp.get().isBlockedIpStatus()){
            // Blok bilgilerini direkt response'da yazdırabilir veya yönlendirme yapabilirsiniz.
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<html><body>"
                    + "<h2>Yöneticiyle İletişime Geçin</h2>"
                    + "<p>IP adresiniz (" + clientIp + ") bloklu.</p>"
                    + "<p>Kalan blok süresi: " + blockedIp.get().getDiffLockedTime() + " dakika.</p>"
                    + "</body></html>");
            return; // İşlem burada sonlanır, diğer filtrelere geçmez.


        }
        filterChain.doFilter(request,response);

    }
}
