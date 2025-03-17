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

    // Login endpoint: Kullanıcı adı ve şifre ile kimlik doğrulaması yapılır
    @PostMapping("/register")
    public ResponseEntity<ResultData<UserResponse>> createUser(@Valid @RequestBody UserSaveRequest request) {
        UserResponse userResponse = userService.createUser(request);
        User user = modelMapper.map(userResponse, User.class);
        //authService.register(user);
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

            //Optional<User> userOptional= userService.getUserByMail(authenticationRequest.getEmail());
           // userService.updateUserLocked(userOptional.get());

            // Kullanıcıyı doğrula ve sessionı dogrula sonra token üret
            User user = authService.authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword(),httpRequest);
            Optional<Session> optionalSession = sessionService.findByEmailAndIpAddressAndDeviceInfo(user.getEmail(),clientIp,userAgent.get("Device"));

            if(optionalSession.isEmpty()){
                    EmailSend emailSend = sessionService.requestOtpIfNeeded(user, clientIp, userAgent);
                    if(emailSend!=null) {
                        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                                .body(ResultHelper.errorWithData("Yeni Oturum Algılandı OTP Doğrulaması için E-posta kutunuzu kontrol edin ", null, HttpStatus.SEE_OTHER));
                    }
            }
            Token accessToken = tokenService.accessToken(user);
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .accessToken(accessToken.getTokenValue())
                    .refreshToken(optionalSession.get().getToken().getTokenValue())
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

    @PostMapping("/otp-session-verification")
    public ResponseEntity<?> otpVerification(@RequestBody VerificationRequest verificationRequest, HttpServletRequest httpRequest) {
        try {
            String value = verificationRequest.getCodeValue();
            String email = verificationRequest.getEmail();
            // IP adresi ve cihaz bilgisi alınır
            String clientIp = IpUtils.getClientIp(httpRequest);
            Map<String, String> userAgent = DeviceUtils.getUserAgent(httpRequest);

            Optional<User> optionalUser = userService.getUserByMail(email);
             if(codeService.isValidateCode(email, value)){
                Token refreshToken = tokenService.refreshToken(optionalUser.get());
                sessionService.createOrUpdateSession(optionalUser.get(), refreshToken, clientIp, userAgent);
               // sessionService.verifyOtpAndEnableSession(email, clientIp, userAgent.get("Device"));
                return ResponseEntity.status(HttpStatus.OK).body(ResultHelper.successWithData("Oturum doğrulandı", null, HttpStatus.OK));
            }else{
                throw new NotFoundException("Gecersiz code: " + value);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultHelper.errorWithData("Oturum doğrulanamadı : "+e.getMessage(), null, HttpStatus.BAD_REQUEST));
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
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ForgotPasswordRequest request,HttpServletRequest httpRequest) {

        try {
            String resetToken = request.getResetPasswordToken();
            String password = request.getPassword();
            String confirmPassword = request.getConfirmPassword();
            String clientIp = IpUtils.getClientIp(httpRequest);
            Map<String, String> userAgent = DeviceUtils.getUserAgent(httpRequest);

            if (jwtService.isTokenExpired(resetToken)) {
                throw new IllegalStateException("Geçersiz veya süresi dolmuş link.");
            }

            Token tokenFind = tokenService.findByTokenValue(resetToken);
            User user = tokenFind.getUser();

            if (user == null) {
                throw new IllegalStateException("Kullanıcı bulunamadi.");
            }
            Optional<Session> optionalSession = sessionService.findByEmailAndIpAddressAndDeviceInfo(user.getEmail(),clientIp,userAgent.get("Device"));
                if (password.equalsIgnoreCase(confirmPassword)) {
                    user.setPassword(password);
                    user.setAccountLockedTime(null);
                    user.setAccountLocked(false);
                    user.setDiffLockedTime(0);
                    user.setIncrementFailedLoginAttempts(0);
                    UserUpdateRequest userUpdateRequest = this.modelMapperService.forRequest().map(user, UserUpdateRequest.class);
                    userService.updateUser(userUpdateRequest);
                    sessionService.terminateSession(optionalSession.get().getId());
                    tokenService.delete(optionalSession.get().getToken());
                    return ResponseEntity.status(HttpStatus.OK).body(ResultHelper.successWithData("Şifreniz başarıyla sıfırlandı: ", user.getEmail(), HttpStatus.OK));

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



    // Refresh token endpoint
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody VerificationRequest verificationRequest, HttpServletRequest request) {
        // Süresi dolmuş token'ları kontrol et ve güncelle
        tokenService.checkAndUpdateExpiredTokens();
        // IP ve Cihaz bilgisi kontrol edilir
        String clientIp = IpUtils.getClientIp(request);
        Map<String, String> userAgent = DeviceUtils.getUserAgent(request);
        if (verificationRequest.getTokenValue() == null || verificationRequest.getTokenValue().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("token degeri boş olamaz!");
        }
        Token refreshToken = tokenService.findByTokenValue(verificationRequest.getTokenValue());
        if(refreshToken!=null){
            String email = jwtService.extractEmail(verificationRequest.getTokenValue());
            Session session = sessionRepository.findByEmailAndIpAddressAndDeviceInfo(email, clientIp, userAgent.get("Device"))
                    .orElseThrow(() -> new RuntimeException("Session bilgisi bulunamadı"));
            // Yeni access token üretilir ve döndürülür
            String newAccessToken = jwtService.generateAccessToken(session.getUser());
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResultHelper.errorWithData("oturum geçerli degil giriş yapın",null,HttpStatus.UNAUTHORIZED));

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
