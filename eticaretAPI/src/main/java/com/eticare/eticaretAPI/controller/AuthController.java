package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.jwt.AuthenticationService;
import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.config.jwt.CustomUserDetailsService;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.AuthRequest.AuthenticationRequest;
import com.eticare.eticaretAPI.dto.request.User.UserSaveRequest;
import com.eticare.eticaretAPI.dto.response.AuthenticationResponse;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.ISessionRepository;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.service.EmailService;
import com.eticare.eticaretAPI.service.SessionService;
import com.eticare.eticaretAPI.service.UserService;
import com.eticare.eticaretAPI.service.VerificationService;
import com.eticare.eticaretAPI.utils.DeviceUtils;
import com.eticare.eticaretAPI.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private VerificationService verificationService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ISessionRepository sessionRepository;
    /**
     * Login endpoint: Kullanıcı adı ve şifre ile kimlik doğrulaması yapılır
     */
    @PostMapping("/register")
    public ResultData<UserResponse> createUser(@RequestBody UserSaveRequest request) {
        UserResponse userResponse = userService.createUser(request);
        User user = modelMapper.map(userResponse, User.class);
        authenticationService.register(user);
        // Doğrulama kodu oluştur ve kullanıcıya gönder


        return ResultHelper.created(userResponse);
        // UserServise sınıfında user sınıfı maplenıyor metot tıpı  UserResponse donuyor bu yuzden burada maplemedık
    }


    @PostMapping("/verifyAccount")
    @PreAuthorize("isAuthenticated()")
    public  ResultData<?> verifyAccount(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String code) {

        try {

        if (userDetails == null || code == null) {
            throw new IllegalStateException("Email veya doğrulama kodu eksik.");
            }
        boolean isVerification =verificationService.activateUser(userDetails.getUsername(),code);
            if (isVerification) {
                return ResultHelper.successWithData("Hesap Dogrulama Başarılı: ",userDetails.getUsername(),HttpStatus.OK);
            } else {
                //return ResultHelper.errorWithData("Kod geçersiz veya süresi dolmuş.", null, HttpStatus.BAD_REQUEST);
                throw new IllegalStateException("Kod geçersiz veya süresi dolmuş  ");
            }
        }catch (Exception e){
            return ResultHelper.errorWithData("Hesap Dogrulanama Başarısız: ",e.getMessage(),HttpStatus.BAD_REQUEST);

        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletRequest httpRequest
    ) throws Exception {

        // Süresi dolmuş token'ları kontrol et ve güncelle
        authenticationService.checkAndUpdateExpiredTokens();
        System.out.println(authenticationRequest.getEmail());
        // Kullanıcıyı doğrula ve token üret
        List<Token> tokens = authenticationService.authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        Token refreshToken = tokens.get(0); // İlk eleman (Refresh Token)
        Token accessToken = tokens.get(1); // İkinci eleman (Access Token)

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        // Kullanıcıyı username(email) ile bul
       User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new RuntimeException("User bulunamadı"));
      /* // Kullanıcı bulunamazsa hata fırlat
            if(!user.isActive())
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hesap Aktif Değil");
            }*/

        // IP adresi ve cihaz bilgisi alınır
        String ipAddress = IpUtils.getClientIp(httpRequest);
        Map<String ,String> deviceInfo = DeviceUtils.getUserAgent(httpRequest);


        // Session oluşturulur
        sessionService.createSession(user, refreshToken, ipAddress, deviceInfo);

        // Yanıt olarak token ve kullanıcı bilgilerini gönder
        return ResponseEntity.ok(new AuthenticationResponse(accessToken.getToken(), userDetails.getUsername(), userDetails.getAuthorities(),user.isActive()));
    }

    // Refresh token endpoint
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody AuthenticationRequest authenticationRequest ,HttpServletRequest httpServletRequest) {
        // Kullanıcı kontrolü
        User user = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı"));

        // Kullanıcıya ait refresh token'ı bul
      /*  Token refreshToken = tokenRepository.findByUserAndTokenType(user, TokenType.REFRESH)
                .orElseThrow(() -> new NotFoundException("Refresh token bulunamadı"));
  // Refresh token doğrulama
        String token = refreshToken.getToken();
        String email = jwtService.extractEmail(token);
    */

        Session session = sessionRepository.findByRefreshToken(authenticationRequest.getEmail()).orElseThrow(()-> new RuntimeException("Session bilgisi bulunamadı"));

        if(sessionService.isValidSession(session.getEmail(),session.getIpAddress(),session.getDevice()))
        {
            if(session.getIpAddress().equalsIgnoreCase(IpUtils.getClientIp(httpServletRequest))
                && session.getDevice().equalsIgnoreCase(DeviceUtils.getUserAgent(httpServletRequest).get("Device"))) {
                if (session.getEmail() == null || !jwtService.isTokenValid(session.getRefreshToken(), session.getEmail())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Refresh token geçersiz veya süresi dolmuş. Lütfen tekrar giriş yapınız.");
                }
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Oturum bilgisi dogrulanamadı");

            }
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Oturum bilgisi bulunamadı");
        }

        // Yeni access token oluştur
        String newAccessToken = jwtService.generateAccessToken(session.getEmail());

        // Yanıt olarak yeni token'ı döndür
        return ResponseEntity.ok()
                .header("New-Access-Token", newAccessToken)
                .body(Map.of("accessToken", newAccessToken)); // JSON formatında döndürmek
    }

}
