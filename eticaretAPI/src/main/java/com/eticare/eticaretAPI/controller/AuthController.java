package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.request.User.UserUpdateRequest;
import com.eticare.eticaretAPI.service.impl.AuthenticationService;
import com.eticare.eticaretAPI.config.jwt.CustomUserDetailsService;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.ActivatedAccount.VerifyCodeRequest;
import com.eticare.eticaretAPI.dto.request.AuthRequest.AuthenticationRequest;
import com.eticare.eticaretAPI.dto.request.ForgotPasswordRequest.ForgotPasswordRequest;
import com.eticare.eticaretAPI.dto.request.User.UserSaveRequest;
import com.eticare.eticaretAPI.dto.response.AuthenticationResponse;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.entity.Session;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.repository.ISessionRepository;
import com.eticare.eticaretAPI.repository.ITokenRepository;
import com.eticare.eticaretAPI.repository.IUserRepository;
import com.eticare.eticaretAPI.service.EmailSendService;
import com.eticare.eticaretAPI.service.SessionService;
import com.eticare.eticaretAPI.service.UserService;
import com.eticare.eticaretAPI.service.CodeService;
import com.eticare.eticaretAPI.utils.DeviceUtils;
import com.eticare.eticaretAPI.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    private IModelMapperService modelMapperService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ITokenRepository tokenRepository;

    @Autowired
    private EmailSendService emailSendService;
    @Autowired
    private CodeService codeService;

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

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest httpRequest
    ) throws Exception {
        // Süresi dolmuş token'ları kontrol et ve güncelle
        authenticationService.checkAndUpdateExpiredTokens();

        // Kullanıcıyı doğrula ve token üret
        List<Token> tokens = authenticationService.authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        Token refreshToken = tokens.get(0); // İlk eleman (Refresh Token)
        Token accessToken = tokens.get(1); // İkinci eleman (Access Token)

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // IP adresi ve cihaz bilgisi alınır
        String ipAddress = IpUtils.getClientIp(httpRequest);
        Map<String, String> deviceInfo = DeviceUtils.getUserAgent(httpRequest);
        // Session oluşturulur
        sessionService.createSession(user, refreshToken, ipAddress, deviceInfo);
        // Yanıt olarak token ve kullanıcı bilgilerini gönder

    /*    new AuthenticationResponse (accessToken.getToken(), userDetails.getUsername(), userDetails.getAuthorities(), user.isActive());*/
        return ResponseEntity.ok(AuthenticationResponse
                .builder().accessToken(accessToken.getToken())
                .email(userDetails.getUsername())
                .roles(userDetails.getAuthorities())
                .isActive(user.isActive())
                .build()
        );
    }


    @PostMapping("/activate-account")
    public ResultData<?> activateAccount(@RequestBody VerifyCodeRequest verifyCodeRequest) {
        String token = verifyCodeRequest.getVerifyToken();
        System.out.println("activasyon token: " + token);

        try {
            boolean expiredToken = jwtService.isTokenExpired(token);
            if (expiredToken) {
                throw new IllegalStateException("Aktivasyon tokenın süresi dolmuş.");
            }

            String email  = jwtService.extractEmail(token);
            if (email.isBlank()) {
                throw new IllegalStateException("Email bilgisi eksik.");
            }

            boolean isVerification = codeService.activateUser(email);
            if (isVerification) {
                return ResultHelper.successWithData("Hesap Dogrulama Başarılı: ", email, HttpStatus.OK);
            } else {
                // Eğer doğrulama başarısızsa hata döndürülür.
                return ResultHelper.errorWithData("Hesap Dogrulama Başarısız: ", "Verifikasyon işlemi başarısız.", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return ResultHelper.errorWithData(e.getMessage(),null , HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/reset-password")
    public ResultData<?> resetPassword(@RequestBody ForgotPasswordRequest request)  {

        try {
            String resetToken =request.getResetPasswordToken();
            String password = request.getPassword();
            String confirmPassword =request.getConfirmPassword();
            Token tokenFind = tokenRepository.findByToken(resetToken).orElseThrow(()-> new RuntimeException("Kullanıcı bulunamadi"));
            User user = tokenFind.getUser();
            System.out.printf(user.toString());
            String email = jwtService.extractEmail(resetToken);
            boolean expiredToken = jwtService.isTokenExpired(resetToken);
            System.out.println("reset token: "+resetToken);
            if (user==null) {
                throw new IllegalStateException("Kullanıcı bulunamadi.");
            }
            if (expiredToken) {
                throw new IllegalStateException("Geçersiz veya süresi dolmuş token.");
            }

            if (!password.isBlank() || !confirmPassword.isBlank()) {
                if(password.equalsIgnoreCase(confirmPassword)){
                    user.setPassword(password);
                    UserUpdateRequest userUpdateRequest = this.modelMapperService.forRequest().map(user, UserUpdateRequest.class);
                    userService.updateUser(userUpdateRequest );
                    tokenRepository.delete(tokenFind);
                    return ResultHelper.successWithData("Şifreniz başarıyla sıfırlandı: ", email, HttpStatus.OK);

                }else{
                    throw new RuntimeException("Şifreler uyuşmuyor");

                }
            } else {
                throw new IllegalStateException("Şifre geçersiz veya boş");
            }

        } catch (Exception e) {
            return ResultHelper.errorWithData("Hesap Dogrulanama Başarısız: ", e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

  /*  @PostMapping("/verifyAccount")
    @PreAuthorize("isAuthenticated()")
    public ResultData<?> verifyAccount(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String code) {

        try {

            if (userDetails == null || code == null) {

            }
            boolean isVerification = verificationService.activateUser(userDetails.getUsername(), code);
            if (isVerification) {
                return ResultHelper.successWithData("Hesap Dogrulama Başarılı: ", userDetails.getUsername(), HttpStatus.OK);
            } else {
                //return ResultHelper.errorWithData("Kod geçersiz veya süresi dolmuş.", null, HttpStatus.BAD_REQUEST);
                throw new IllegalStateException("Kod geçersiz veya süresi dolmuş  ");
            }
        } catch (Exception e) {
            return ResultHelper.errorWithData("Hesap Dogrulanama Başarısız: ", e.getMessage(), HttpStatus.BAD_REQUEST);

        }

    }*/



    // Refresh token endpoint
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest) {
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

        Session session = sessionRepository.findByRefreshToken(user.getEmail()).orElseThrow(() -> new RuntimeException("Session bilgisi bulunamadı"));

        if (sessionService.isValidSession(session.getEmail(), session.getIpAddress(), session.getDevice())) {
            if (session.getIpAddress().equalsIgnoreCase(IpUtils.getClientIp(httpServletRequest))
                    && session.getDevice().equalsIgnoreCase(DeviceUtils.getUserAgent(httpServletRequest).get("Device"))) {
                if (session.getEmail() == null || !jwtService.isTokenValid(session.getRefreshToken())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Refresh token geçersiz veya süresi dolmuş. Lütfen tekrar giriş yapınız.");
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Oturum bilgisi dogrulanamadı");

            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Oturum bilgisi bulunamadı");
        }

        // Yeni access token oluştur
        String newAccessToken = jwtService.generateAccessToken(userService.getUserByMail(session.getEmail()).get());

        // Yanıt olarak yeni token'ı döndür
        return ResponseEntity.ok()
                .header("New-Access-Token", newAccessToken)
                .body(Map.of("accessToken", newAccessToken)); // JSON formatında döndürmek
    }

}
