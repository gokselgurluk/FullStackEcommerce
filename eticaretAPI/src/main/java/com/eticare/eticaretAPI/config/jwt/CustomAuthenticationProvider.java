package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.entity.EmailSend;
import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.RoleEnum;
import com.eticare.eticaretAPI.service.SessionService;
import com.eticare.eticaretAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserDetailsService(CustomUserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        Map<String,String> details =(Map<String, String>) authentication.getDetails();
        String clientIp = details.get("Ip");
        String clientDevice =details.get("Device");



        Optional<User> userOptional = userService.getUserByMail(email);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Bu e-posta ile kayıtlı kullanıcı bulunamadı: " + email);
        }
        User user = userOptional.get();
        RoleEnum role = user.getRoleEnum();
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));



        // Eğer kilit süresi varsa veya şu anki zamandan sonra ise hesap kilitlidir.
        if(userOptional.get().isAccountLocked()){
            userService.diffLockedTime(userOptional.get());
            return new UsernamePasswordAuthenticationToken(userOptional.get(), null, authorities);

            //throw new LockedException("Hesap kilitli beklemeniz gereken süre : "+ userOptional.get().getDiffLockedTime() +" dakika");

        }


        if (user.getPassword() == null) {
            throw new BadCredentialsException("Şifre bilgisi eksik.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            if(sessionService.isSessionValid(email,clientIp,clientDevice)){
                sessionService.incrementFailedLoginAttempts(email);
            }else{
                userService.handleFailedLogin(user);
            }
            userService.updateUserLocked(userOptional.get()); // Burada güncelle
            throw new BadCredentialsException("Şifre yanlış");
        }



        userService.resetFailedLoginAttempts(user);

        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }
}
