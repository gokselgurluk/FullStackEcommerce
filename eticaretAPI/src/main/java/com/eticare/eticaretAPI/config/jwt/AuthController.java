package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.AuthRequest.AuthenticationRequest;
import com.eticare.eticaretAPI.dto.request.User.UserSaveRequest;
import com.eticare.eticaretAPI.dto.response.AuthenticationResponse;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
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
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JwtService jwtService;

    /**
     * Login endpoint: Kullanıcı adı ve şifre ile kimlik doğrulaması yapılır
     */
    @PostMapping("/register")
    public ResultData<UserResponse> createUser(@RequestBody UserSaveRequest request) {
        UserResponse userResponse = userService.createUser(request);
        User user = modelMapper.map(userResponse, User.class);
        authenticationService.register(user);
        return ResultHelper.created(userResponse);
        // UserServise sınıfında user sınıfı maplenıyor metot tıpı  UserResponse donuyor bu yuzden burada maplemedık
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        // Süresi dolmuş token'ları kontrol et ve güncelle
        authenticationService.checkAndUpdateExpiredTokens();

        // Kullanıcıyı doğrula ve token üret
        String token = authenticationService.authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

        // Kullanıcıyı username(email) ile bul
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());

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

    // Refresh token endpoint
    @PostMapping("/refresh-token")
    public void refreshToken(@RequestHeader("Refresh-Token") String refreshToken, HttpServletResponse response) {
        // Refresh token geçerli mi kontrol et
        String email = jwtService.extractEmail(refreshToken);

        if (email != null && jwtService.isTokenValid(refreshToken, email)) {
            // Yeni access token oluştur
            String newAccessToken = jwtService.generateAccessToken(email);

            // Yeni access token'ı header'a ekle
            response.setHeader("New-Access-Token", newAccessToken);
        } else {
            // Refresh token geçerli değilse, yetkisiz erişim hatası ver
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try {
                response.getWriter().write("Refresh token is invalid or expired. Please login again.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
