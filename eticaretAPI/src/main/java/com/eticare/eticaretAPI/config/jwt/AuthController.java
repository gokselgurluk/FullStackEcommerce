package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.dto.request.AuthRequest.AuthenticationRequest;
import com.eticare.eticaretAPI.dto.response.AuthenticationResponse;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {



    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private IUserRepository userRepository;
    /**
     * Login endpoint: Kullanıcı adı ve şifre ile kimlik doğrulaması yapılır
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        // Süresi dolmuş token'ları kontrol et ve güncelle
        authenticationService.checkAndUpdateExpiredTokens();

        // Kullanıcıyı doğrula ve token üret
        String token = authenticationService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

// Kullanıcıyı username ile bul
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());

// Kullanıcı varsa, lastLogin bilgisini güncelle
        user.ifPresent(userEntity -> {
            userEntity.setLastLogin(new Date());
            userRepository.save(userEntity); // sadece ilgili kullanıcıyı kaydediyor
        });

// Kullanıcı bulunamazsa hata fırlat
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        // Yanıt olarak token ve kullanıcı bilgilerini gönderme
        return ResponseEntity.ok(new AuthenticationResponse(token, userDetails.getUsername(), userDetails.getAuthorities()));
    }

}
