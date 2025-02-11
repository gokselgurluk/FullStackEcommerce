package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.RoleEnum;
import com.eticare.eticaretAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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
import java.util.Optional;
@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {


    @Autowired
    private UserService userService;

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

        Optional<User> userOptional = userService.getUserByMail(email);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Bu e-posta ile kayıtlı kullanıcı bulunamadı: " + email);
        }

        User user = userOptional.get();

        if (user.getPassword() == null) {
            throw new BadCredentialsException("Şifre bilgisi eksik.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            userService.handleFailedLogin(user);
            throw new BadCredentialsException("Şifre yanlış");
        }

        RoleEnum role = user.getRoleEnum();
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));

        userService.resetFailedLoginAttempts(user);

        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }
}
