package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.exeption.NotFoundException;
import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.request.User.UserUpdateRequest;
import com.eticare.eticaretAPI.entity.*;
import com.eticare.eticaretAPI.service.*;
import com.eticare.eticaretAPI.service.impl.AuthService;
import com.eticare.eticaretAPI.config.jwt.CustomUserDetailsService;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.ActivatedAccount.VerificationRequest;
import com.eticare.eticaretAPI.dto.request.AuthRequest.AuthenticationRequest;
import com.eticare.eticaretAPI.dto.request.ForgotPasswordRequest.ForgotPasswordRequest;
import com.eticare.eticaretAPI.dto.request.User.UserSaveRequest;
import com.eticare.eticaretAPI.dto.response.AuthenticationResponse;
import com.eticare.eticaretAPI.dto.response.UserResponse;
import com.eticare.eticaretAPI.repository.ISessionRepository;
import com.eticare.eticaretAPI.utils.DeviceUtils;
import com.eticare.eticaretAPI.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final CustomUserDetailsService userDetailsService;
    private final AuthService authService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final IModelMapperService modelMapperService;
    private final JwtService jwtService;

    private final EmailSendService emailSendService;
    private final CodeService codeService;
    private final SessionService sessionService;
    private final ISessionRepository sessionRepository;
    private final TokenService tokenService;

    public AuthController(CustomUserDetailsService userDetailsService, AuthService authService, UserService userService, ModelMapper modelMapper, IModelMapperService modelMapperService, JwtService jwtService, EmailSendService emailSendService, CodeService codeService, SessionService sessionService, ISessionRepository sessionRepository, TokenService tokenService) {
        this.userDetailsService = userDetailsService;
        this.authService = authService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.modelMapperService = modelMapperService;
        this.jwtService = jwtService;

        this.emailSendService = emailSendService;
        this.codeService = codeService;
        this.sessionService = sessionService;
        this.sessionRepository = sessionRepository;
        this.tokenService = tokenService;
    }

    /**
     * Login endpoint: Kullanıcı adı ve şifre ile kimlik doğrulaması yapılır
     */
    @PostMapping("/register")
    public ResponseEntity<ResultData<UserResponse>> createUser(@Valid @RequestBody UserSaveRequest request) {
        UserResponse userResponse = userService.createUser(request);
        User user = modelMapper.map(userResponse, User.class);
        authService.register(user);
        // Doğrulama kodu oluştur ve kullanıcıya gönder
        return ResponseEntity.status(HttpStatus.OK).body(ResultHelper.created(userResponse));
        // UserServise sınıfında user sınıfı maplenıyor metot tıpı  UserResponse donuyor bu yuzden burada maplemedık
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest httpRequest) {

        try {
            // Süresi dolmuş token'ları kontrol et ve güncelle
            tokenService.checkAndUpdateExpiredTokens();
            // IP adresi ve cihaz bilgisi alınır
            String clientIp = IpUtils.getClientIp(httpRequest);
            Map<String, String> userAgent = DeviceUtils.getUserAgent(httpRequest);
            Optional<User> userOptional= userService.getUserByMail(authenticationRequest.getEmail());
            if (userOptional.get().isAccountLocked()) {
                System.out.println("bloklu hesap algılandı");
                return ResponseEntity.status(HttpStatus.LOCKED).body(ResultHelper.errorWithData("Hesap kilidini açmak için otp kodunu giriniz", "beklemeniz gereken süre : " + userOptional.get().getDiffLockedTime() + " dakika", HttpStatus.LOCKED));
            }

            // Kullanıcıyı doğrula ve sessionı dogrula sonra token üret
            User user = authService.authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword(),httpRequest);


            Token refreshToken = tokenService.refreshToken(user);

            if(!sessionService.isSessionValid(user.getEmail(),clientIp,userAgent.get("Device"))){
                EmailSend emailSend = sessionService.requestOtpIfNeeded(user, refreshToken, clientIp, userAgent);
                if(emailSend!=null) {
                    return ResponseEntity.status(HttpStatus.SEE_OTHER)
                            .body(ResultHelper.successWithData("Yeni IP algılandı OTP Doğrulaması için E-posta kutunuzu kontrol edin: ", emailSend.getEmailExpiryDate(), HttpStatus.SEE_OTHER));
                }
            }
            // Session Güncelle
            sessionService.createOrUpdateSession(user, refreshToken, clientIp, userAgent);
            Token accessToken = tokenService.accessToken(user);
            // Yanıt olarak token ve kullanıcı bilgilerini gönder
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .accessToken(accessToken.getTokenValue())
                    .email(user.getEmail())
                    .role(user.getRoleEnum())
                    .isActive(user.isActive())
                    .build();

            return ResponseEntity.ok(response);
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(ResultHelper.errorWithData(e.getMessage(), null, HttpStatus.LOCKED));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultHelper.errorWithData(e.getMessage(), null, HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/otp-verification")
    public ResponseEntity<?> otpVerification(@RequestBody VerificationRequest verificationRequest, HttpServletRequest httpRequest) {
        try {
            String value = verificationRequest.getCodeValue();
            String email = verificationRequest.getEmail();
            // IP adresi ve cihaz bilgisi alınır
            String clientIp = IpUtils.getClientIp(httpRequest);
            Map<String, String> userAgent = DeviceUtils.getUserAgent(httpRequest);

            if(codeService.isValidateCode(email, value)){
                sessionService.verifyOtpAndEnableSession(email, clientIp, userAgent.get("Device"));
                return ResponseEntity.status(HttpStatus.OK).body("Oturum başarıyla doğrulandı.");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResultHelper.Error500("Oturum dogrulanamadı."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResultHelper.errorWithData(e.getMessage(), null, HttpStatus.FORBIDDEN));
        }

    }

    @PostMapping("/activate-account")
    public ResponseEntity<?> activateAccount(@RequestBody VerificationRequest verificationRequest) {
        String token = verificationRequest.getTokenValue();

        try {
            boolean expiredToken = jwtService.isTokenExpired(token);
            if (expiredToken) {
                throw new IllegalStateException("Aktivasyon tokenın süresi dolmuş.");
            }

            String email = jwtService.extractEmail(token);
            if (email.isBlank()) {
                throw new IllegalStateException("Email bilgisi eksik.");
            }

            boolean isVerification = codeService.activateUser(email);
            if (isVerification) {
                return ResponseEntity.status(HttpStatus.OK).body(ResultHelper.successWithData("Hesap Dogrulama Başarılı: ", email, HttpStatus.OK));
            } else {
                // Eğer doğrulama başarısızsa hata döndürülür.
                throw new IllegalStateException("Hesap Dogrulama Başarısız: ");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultHelper.errorWithData(e.getMessage(), null, HttpStatus.BAD_REQUEST));
        }

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ForgotPasswordRequest request) {

        try {
            String resetToken = request.getResetPasswordToken();
            String password = request.getPassword();
            String confirmPassword = request.getConfirmPassword();
            Token tokenFind = tokenService.findByTokenValue(resetToken);
            User user = tokenFind.getUser();
            if (user == null) {
                throw new IllegalStateException("Kullanıcı bulunamadi.");
            }
            String email = jwtService.extractEmail(resetToken);
            boolean expiredToken = jwtService.isTokenExpired(resetToken);


            if (expiredToken) {
                throw new IllegalStateException("Geçersiz veya süresi dolmuş token.");
            }


                if (password.equalsIgnoreCase(confirmPassword)) {
                    user.setPassword(password);
                    UserUpdateRequest userUpdateRequest = this.modelMapperService.forRequest().map(user, UserUpdateRequest.class);
                    userService.updateUser(userUpdateRequest);
                    tokenService.delete(tokenFind);
                    return ResponseEntity.status(HttpStatus.OK).body(ResultHelper.successWithData("Şifreniz başarıyla sıfırlandı: ", email, HttpStatus.OK));

                } else {
                    throw new RuntimeException("Şifreler uyuşmuyor");

                }
            }

            catch (ValidationException e){
                throw new IllegalStateException("Şifre geçersiz veya boş");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultHelper.errorWithData("Şifre degişimi başarısız : ", e.getMessage(), HttpStatus.BAD_REQUEST));
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
        User user = userService.getUserByMail(authenticationRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı"));

        // Kullanıcıya ait refresh token'ı bul
      /*  Token refreshToken = tokenRepository.findByUserAndTokenType(user, TokenType.REFRESH)
                .orElseThrow(() -> new NotFoundException("Refresh token bulunamadı"));
  // Refresh token doğrulama
        String token = refreshToken.getToken();
        String email = jwtService.extractEmail(token);
    */

        Session session = sessionRepository.findByRefreshToken(user.getEmail()).orElseThrow(() -> new RuntimeException("Session bilgisi bulunamadı"));

        if (sessionService.isSessionValid(session.getEmail(), session.getIpAddress(), session.getDeviceInfo())) {
            if (session.getIpAddress().equalsIgnoreCase(IpUtils.getClientIp(httpServletRequest))
                    && session.getDeviceInfo().equalsIgnoreCase(DeviceUtils.getUserAgent(httpServletRequest).get("Device"))) {
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
