package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.dto.request.AuthRequest.AuthenticationRequest;
import com.eticare.eticaretAPI.dto.response.AuthenticationResponse;
import com.eticare.eticaretAPI.config.jwt.CustomUserDetailsService;
import com.eticare.eticaretAPI.config.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/auth")
public class AuthController {
@Autowired
private AuthenticationManager authenticationManager;

@Autowired
private JwtTokenUtil jwtTokenUtil;

@Autowired
private CustomUserDetailsService userDetailsService;  // Burada CustomUserDetailsService'yi ekledik

@PostMapping("/login")
public ResponseEntity<?> createAuthToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
    authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

    // UserDetailsService ile kullanıcının detaylarını yükleyin
    final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
    final String token = jwtTokenUtil.generateToken(String.valueOf(userDetails));

    return ResponseEntity.ok(new AuthenticationResponse(token));
}

private void authenticate(String username, String password) throws Exception {
    try {
        // Kullanıcı adı ve şifreyi doğrulamak için AuthenticationManager kullanılır
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (BadCredentialsException e) {
        // Şifre yanlış olduğunda veya kullanıcı bulunamadığında hata döndürülür
        throw new Exception("INVALID_CREDENTIALS", e);
    }
}
}