package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.IUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private  final  IUserRepository userRepository;

    public CustomUserDetailsService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Burada kullanıcıyı veritabanından alacağınız kodu yazabilirsiniz
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> users = this.userRepository.findByUserName(username);
        // Kullanıcı adını kontrol et, örneğin sabit bir kullanıcı verisi döndürelim
        if ("user".equals(username)) {
            return new CustomUserDetails("user", "{noop}password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        } else if ("admin".equals(username)) {
            return new CustomUserDetails("admin", "{noop}admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }

        throw new UsernameNotFoundException("User not found");
    }
}
