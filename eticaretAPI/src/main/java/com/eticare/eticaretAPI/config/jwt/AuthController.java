package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.config.result.ResultMessages;
import com.eticare.eticaretAPI.dto.request.AuthRequest.AuthenticationRequest;
import com.eticare.eticaretAPI.dto.request.User.UserSaveRequest;
import com.eticare.eticaretAPI.dto.response.AuthenticationResponse;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerificationToken;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.service.EmailService;
import com.eticare.eticaretAPI.service.UserService;
import com.eticare.eticaretAPI.service.VerificationTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.Map;
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
    @Autowired
    private ITokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private VerificationTokenService verificationTokenService;
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


    @PostMapping("/verify")
    @PreAuthorize("isAuthenticated()")
    public  ResultData<?> verifyAccount(@RequestParam Map<String,String> params ) {
        String email = params.get("email");
        String code = params.get("code");
        if (email == null || code == null) {
            return ResultHelper.errorWithData("Email ve doğrulama kodu gereklidir.", params, HttpStatus.BAD_REQUEST);
        }
            if (authenticationService.activateUser(email,code)) {
                return ResultHelper.success("Hesap başarıyla doğrulandı!");
            } else {
                return ResultHelper.errorWithData("Kod geçersiz veya süresi dolmuş.", null, HttpStatus.BAD_REQUEST);
            }
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
    public ResponseEntity<?> refreshToken(@RequestBody AuthenticationRequest authenticationRequest) {
        // Kullanıcı kontrolü
        User user = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı"));

        // Kullanıcıya ait refresh token'ı bul
        Token refreshToken = tokenRepository.findByUserAndTokenType(user, TokenType.REFRESH)
                .orElseThrow(() -> new NotFoundException("Refresh token bulunamadı"));

        // Refresh token doğrulama
        String token = refreshToken.getToken();
        String email = jwtService.extractEmail(token);

        if (email == null || !jwtService.isTokenValid(token, email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token geçersiz veya süresi dolmuş. Lütfen tekrar giriş yapınız.");
        }

        // Yeni access token oluştur
        String newAccessToken = jwtService.generateAccessToken(email);

        // Yanıt olarak yeni token'ı döndür
        return ResponseEntity.ok()
                .header("New-Access-Token", newAccessToken)
                .body(Map.of("accessToken", newAccessToken)); // JSON formatında döndürmek
    }

    /*
     @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> requestBody)  {
        // requestBody'den access token'ı al
        String accessToken = requestBody.get("accessToken");
        System.out.println("token " + accessToken);
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token bulunamadı.");
        }
        // Access token'dan e-posta adresini çıkar
        String extractEmail = jwtService.extractEmail(accessToken);
        if (extractEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Geçersiz token.");
        }
        System.out.println("Email from token: " + extractEmail);
        // Kullanıcı kontrolü
        User user = userRepository.findByEmail(extractEmail)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı"));
        System.out.println("Email from token: " + user.getUsername());
        // Kullanıcıya ait refresh token'ı bul
        Token refreshToken = tokenRepository.findByUserAndTokenType(user, TokenType.REFRESH)
                .orElseThrow(() -> new NotFoundException("Refresh token bulunamadı"));

        // Refresh token doğrulama
        String token = refreshToken.getToken();


        if (!jwtService.isTokenValid(token, extractEmail)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token geçersiz veya süresi dolmuş. Lütfen tekrar giriş yapınız.");
        }

        // Yeni access token oluştur
        String newAccessToken = jwtService.generateAccessToken(extractEmail);

        // Yanıt olarak yeni token'ı döndür
        return ResponseEntity.ok()
                .header("New-Access-Token", newAccessToken)
                .body(Map.of("accessToken", newAccessToken)); // JSON formatında döndürmek
    }
}
    * */
}
