package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.entity.BlockedIp;
import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.RoleEnum;
import com.eticare.eticaretAPI.service.BlockedIpService;
import com.eticare.eticaretAPI.service.FailedAttemptService;
import com.eticare.eticaretAPI.service.SessionService;
import com.eticare.eticaretAPI.service.UserService;
import com.eticare.eticaretAPI.utils.DeviceUtils;
import com.eticare.eticaretAPI.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {


    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private FailedAttemptService failedAttemptService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BlockedIpService blockedIpService;

    @Autowired
    public void setUserDetailsService(CustomUserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        HttpServletRequest httpRequest = (HttpServletRequest) authentication.getDetails();
        String clientIp = IpUtils.getClientIp(httpRequest);
        Map<String, String> userAgent = DeviceUtils.getUserAgent(httpRequest);

        BlockedIp blockedIp = blockedIpService.blockedIpCreate(clientIp);
        failedAttemptService.createOrUpdateFailedAttempt(email, blockedIp, userAgent);
        Optional<User> userOptional = userService.getUserByMail(email);

        if (userOptional.isEmpty()) {
            failedAttemptService.recordFailedAttempts(email);
            throw new UsernameNotFoundException("Bu e-posta ile kayıtlı kullanıcı bulunamadı: " + email);
        }

        userService.updateUserLocked(userOptional.get());
        if (userOptional.get().isAccountLocked()) {
            userService.diffLockedTime(userOptional.get());
            throw new LockedException("Hesap bloklu şifrenizi sıfırlayın veya beklemeniz gereken süre : " + userOptional.get().getDiffLockedTime() + " dakika");
        }

        if (userOptional.get().getPassword() == null) {
            throw new BadCredentialsException("Şifre bilgisi eksik.");
        }

        if (!passwordEncoder.matches(password,userOptional.get().getPassword())) {
            Optional<Session> optionalSession = sessionService.findByEmailAndIpAddressAndDeviceInfo(email, clientIp, userAgent.get("Device"));
            if (optionalSession.isPresent()) {
                userService.handleFailedLogin(userOptional.get());
                sessionService.incrementFailedLoginAttempts(email, clientIp, userAgent.get("Device"));
            } else {
                failedAttemptService.recordFailedAttempts(email);
            }
            throw new BadCredentialsException("Şifre yanlış");
        }
        User user = userOptional.get();
        RoleEnum role = user.getRoleEnum();
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }
}

   /*     // Eğer kilit süresi varsa veya şu anki zamandan sonra ise hesap kilitlidir.
        if (userOptional.get().isAccountLocked() && sessionService.isSessionValid(email, clientIp, userAgent.get("Device"))) {
            CustomUserDetails userDetails = new CustomUserDetails(
                    user.getEmail(),
                    user.getPassword(),
                    authorities,
                    user.getAccountLockedTime()
            );
            userService.diffLockedTime(userOptional.get());
            return new UsernamePasswordAuthenticationToken(userDetails, password, authorities);

        }*/