package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userService.getUserByMail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));
        // Eğer kilit süresi varsa veya şu anki zamandan sonra ise hesap kilitlidir.
        boolean isUnlocked = user.getAccountLockedTime() == null || user.getAccountLockedTime().before(new Date());
        userService.updateUserLocked(user, isUnlocked); // Burada güncelle
        return new CustomUserDetails(
                userService,
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRoleEnum().name())),
                user.getAccountLockedTime()


        );
    }

}

