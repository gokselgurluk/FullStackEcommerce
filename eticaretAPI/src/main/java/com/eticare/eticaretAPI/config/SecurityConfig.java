package com.eticare.eticaretAPI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private PasswordEncoder passwordEncoder;

    public SecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // UserDetailsService Bean'i
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user").password("{noop}password").roles("USER").build());
        manager.createUser(User.withUsername("admin").password("{noop}admin").roles("ADMIN").build());
        return manager;
    }

    // Security yapılandırması
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF korumasını devre dışı bırak
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Admin sadece admin rolündeki kullanıcılar için
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // User ve Admin için
                        .requestMatchers("/public/**").permitAll() // Public herkese açık
                        .anyRequest().authenticated() // Diğer tüm istekler kimlik doğrulama gerektirir
                )
                .formLogin(formLogin -> formLogin.disable()) // Varsayılan giriş formunu devre dışı bırak
                .logout(logout -> logout.permitAll()); // Çıkış yapabilmek için herkese izin ver

        return http.build(); // SecurityFilterChain'ı döndürüyoruz
    }

}


   /* @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF korumasını devre dışı bırak
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Tüm istekler için izin ver
                )
                .formLogin(formLogin -> formLogin.disable()); // Varsayılan giriş formunu devre dışı bırak
        return http.build();
    }
}*/
