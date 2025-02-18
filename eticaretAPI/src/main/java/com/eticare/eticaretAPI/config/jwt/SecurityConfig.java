package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.service.BlockedIpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final BlockedIpService blockedIpService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, BlockedIpService blockedIpService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;

        this.blockedIpService = blockedIpService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  // CSRF korumasını devre dışı bırak
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/refresh-token", "/auth/login", "/auth/register",
                                "/auth/otp-verification", "/send-activation-email", "/auth/activate-account",
                                "auth/reset-password", "api/forgot-password", "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**", "/Message").permitAll()  // "Message" endpoint'ini herkesin erişmesine izin ver
                        .anyRequest().authenticated()  // Diğer endpoint'ler için doğrulama gerekli
                )            // Özel IP kontrol filtresini, UsernamePasswordAuthenticationFilter'dan önce ekleyin.
                // Önce IP bloklama filtresini çalıştır
                .addFilterBefore(new BlockedIpFilter(blockedIpService), UsernamePasswordAuthenticationFilter.class)
                // Sonra JWT kimlik doğrulama filtresini çalıştır
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(Customizer.withDefaults());

               // .addFilterBefore( UsernamePasswordAuthenticationFilter.class); // JWT doğrulama filtresi

        return http.build();
    }


    // Password encoder bean (required for authentication)/**/
    // AuthenticationManager bean definition
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);  // Use PasswordEncoder

        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, CustomAuthenticationProvider customAuthProvider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(customAuthProvider)
                .build();
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
/*
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       return http
               .csrf(AbstractHttpConfigurer::disable) // CSRF kapatıldı
               .cors(cors -> cors.configure(http)) // CORS açık olmalı
               .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless mod
               .authorizeHttpRequests(auth -> auth
                       .requestMatchers("/auth/**", "/send-activation-email", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                       .anyRequest().authenticated()
               )
               .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT doğrulama filtresi
               .build();
   }
*/
