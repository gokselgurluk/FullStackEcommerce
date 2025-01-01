package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.dto.request.AuthRequest.AuthenticationRequest;
import com.eticare.eticaretAPI.dto.response.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {



    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationService authenticationService;
    /**
     * Login endpoint: Kullanıcı adı ve şifre ile kimlik doğrulaması yapılır
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        // Kullanıcıyı doğrula ve token üret
        String token = authenticationService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        // Kullanıcı detaylarını yükle
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        // Yanıt olarak token ve kullanıcı bilgilerini gönderme
        return ResponseEntity.ok(new AuthenticationResponse(token, userDetails.getUsername(), userDetails.getAuthorities()));
    }

}
