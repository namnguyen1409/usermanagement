package com.namnguyen1409.usermanagement.service.impl;

import com.namnguyen1409.usermanagement.constants.enums.UserRole;
import com.namnguyen1409.usermanagement.dto.RefreshTokenInfo;
import com.namnguyen1409.usermanagement.dto.request.CreateUserRequest;
import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.LoginRequest;
import com.namnguyen1409.usermanagement.dto.request.LogoutRequest;
import com.namnguyen1409.usermanagement.dto.response.CreateUserResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginResponse;
import com.namnguyen1409.usermanagement.dto.response.RefreshTokenResponse;
import com.namnguyen1409.usermanagement.entity.*;
import com.namnguyen1409.usermanagement.exception.AppException;
import com.namnguyen1409.usermanagement.exception.ErrorCode;
import com.namnguyen1409.usermanagement.mapper.UserMapper;
import com.namnguyen1409.usermanagement.repository.LoginLogRepository;
import com.namnguyen1409.usermanagement.repository.TokenBlacklistRepository;
import com.namnguyen1409.usermanagement.repository.UserRepository;
import com.namnguyen1409.usermanagement.service.AuthenticationService;
import com.namnguyen1409.usermanagement.service.ProfileService;
import com.namnguyen1409.usermanagement.service.cache.RefreshTokenCacheService;
import com.namnguyen1409.usermanagement.service.cache.TokenBlackListCacheService;
import com.namnguyen1409.usermanagement.utils.LogUtils;
import com.namnguyen1409.usermanagement.utils.SecurityUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua_parser.Client;
import ua_parser.Parser;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    UserRepository userRepository;
    TokenBlacklistRepository tokenBlacklistRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    SecurityUtils securityUtils;
    LoginLogRepository loginLogRepository;
    LogUtils logUtils;
    HttpServletRequest httpServletRequest;
    ProfileService profileService;
    RefreshTokenCacheService refreshTokenCacheService;
    TokenBlackListCacheService tokenBlackListCacheService;


    @NonFinal
    NimbusJwtDecoder nimbusJwtDecoder;

    @NonFinal
    @Value("${jwt.expiration-time}")
    long expiredTime;

    @NonFinal
    @Value("${jwt.refresh-time}")
    long refreshTime;

    @NonFinal
    @Value("${user.locked-time}")
    long lockedTime;

    @NonFinal
    @Value("${user.max-failed-attempts}")
    int maxFailedAttempts;

    @Override
    public CreateUserResponse register(CreateUserRequest request) {

        log.info("Registering user: {}", logUtils.logObject(request,
                CreateUserRequest.Fields.password,
                CreateUserRequest.Fields.email)
        );
        User user = userMapper.toUser(request);
        user.setIsLocked(false);
        user.setIsDeleted(false);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(Role.builder().name(UserRole.USER).build()));
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.REGISTER_FALSE);
        }
        log.info("User registered successfully: {}", user.getId());
        return CreateUserResponse.builder().success(true).build();
    }

    @Transactional(noRollbackFor = {AppException.class})
    @Override
    public LoginResponse login(LoginRequest request) throws AppException {
        log.info("Login request: {}", logUtils.logObject(request, LoginRequest.Fields.password));
        User user = validateUserOrThrow(request);

        // init login log
        LoginLog loginLog = new LoginLog();
        buildLoginLog(loginLog, user);

        // handle password false
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleInvalidPassword(user, loginLog);
        }

        // handle login success
        loginLog.setSuccess(true);
        String jti = UUID.randomUUID().toString();

        // create jwt token
        String token = generateToken(user, jti);
        loginLog.setJti(jti);


        var refreshToken = createRefreshTokenIfRememberMe(
                request.getRememberMe(), user, loginLog
        );

        try {
            loginLogRepository.save(loginLog);
            log.info("User '{}' logged in successfully. LogId: {}", user.getUsername(), loginLog.getId());
            if (refreshToken != null) {
                refreshTokenCacheService.addRefreshToken(
                        refreshToken,
                        RefreshTokenInfo.builder()
                                .userId(user.getId())
                                .sessionId(loginLog.getId())
                                .expiresAt(LocalDateTime.now().plusSeconds(refreshTime))
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("Error while saving login log for user: {}", user.getUsername());
        }


        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .loginLogId(loginLog.getId())
                .isAuthenticated(true)
                .build();
    }


    private User validateUserOrThrow(LoginRequest request) {
        var user = userRepository.findByUsernameAndIsDeletedFalse(request.getUsername()).orElseThrow(
                () -> new AppException(ErrorCode.INVALID_LOGIN_INFO)
        );
        checkUserLocked(user);
        return user;
    }

    private void handleInvalidPassword(User user, LoginLog loginLog) {
        loginLog.setLogout(true);
        loginLog.setSuccess(false);
        loginLog.setExpiredAt(LocalDateTime.now());
        loginLogRepository.save(loginLog);

        if (isNConsecutiveFailedLoginAttempts(user, maxFailedAttempts)) {
            user.setIsLocked(true);
            user.setLockedAt(LocalDateTime.now());
            userRepository.save(user);
            String lockedUntil = user.getLockedAt()
                    .plusMinutes(lockedTime)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            log.warn("User '{}' is locked until {}", user.getUsername(), lockedUntil);
            throw new AppException(ErrorCode.USER_LOCKED,
                    String.format(ErrorCode.USER_LOCKED.getMessage(), lockedUntil));
        }
        log.warn("Invalid password for user '{}'", user.getUsername());
        throw new AppException(ErrorCode.INVALID_CREDENTIALS);
    }

    private String createRefreshTokenIfRememberMe(
            boolean rememberMe,
            User user,
            LoginLog loginLog
    ) {
        if (rememberMe) {
            var refreshToken = UUID.randomUUID().toString();
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .user(user)
                    .token(refreshToken)
                    .loginLog(loginLog)
                    .expiresAt(LocalDateTime.now().plusSeconds(refreshTime))
                    .revoked(false)
                    .build();
            loginLog.setRefreshToken(refreshTokenEntity);
            loginLog.setExpiredAt(LocalDateTime.now().plusSeconds(refreshTime));
            securityUtils.setRefreshTokenCookie(refreshToken);
            return refreshToken;
        } else {
            loginLog.setExpiredAt(LocalDateTime.now().plusSeconds(expiredTime));
            return null;
        }
    }


    @Override
    public void logout(LogoutRequest request) {
        try {
            String token = request.getToken();
            String jti = extractJtiFromToken(token);

            checkIfBlacklisted(jti);

            var loginLog = getLoginLog(jti);
            blacklistToken(jti, loginLog);
            securityUtils.revokeRefreshTokenIfExists(loginLog);

            loginLogRepository.save(loginLog);
            securityUtils.clearRefreshTokenCookie();
        } catch (Exception e) {
            log.error("Error while logging out", e);
            throw new AppException(ErrorCode.LOGOUT_FAILED);
        }
    }

    private String extractJtiFromToken(String token) {
        if (nimbusJwtDecoder == null) {
            nimbusJwtDecoder = NimbusJwtDecoder.withPublicKey(securityUtils.loadPublicKey()).build();
        }
        Jwt jwt = nimbusJwtDecoder.decode(token);
        return jwt.getClaims().get("jti").toString();
    }

    private void checkIfBlacklisted(String jti) {
        if (tokenBlackListCacheService.isTokenInBlackList(jti)) {
            log.error("Token is already blacklisted");
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    private LoginLog getLoginLog(String jti) {
        return loginLogRepository.findByJti(jti)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
    }

    private void blacklistToken(String jti, LoginLog loginLog) {
        TokenBlacklist tokenBlacklist = TokenBlacklist.builder()
                .tokenId(jti)
                .expiredAt(loginLog.getExpiredAt())
                .build();
        tokenBlacklistRepository.save(tokenBlacklist);
        tokenBlackListCacheService.addTokenToBlackList(jti);
        loginLog.setLogout(true);
    }



    @Transactional
    @Override
    public RefreshTokenResponse refreshToken() {
        try {
            String token = securityUtils.getRefreshTokenFromCookie();
            log.info("Refresh token: {}", token);
            RefreshTokenInfo refreshToken = refreshTokenCacheService.getRefreshToken(token);
            if (refreshToken == null) {
                log.error("Refresh token revoked or not found for token: {}", token);
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
            if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.error("Refresh token expired for user: {}", refreshToken.getUserId());
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }

            var loginLog = loginLogRepository.findById(refreshToken.getSessionId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

            var user = loginLog.getUser();
            securityUtils.checkUserDeleted(user);
            String jti = UUID.randomUUID().toString();
            String newToken = generateToken(user, jti);
            tokenBlacklistRepository.save(new TokenBlacklist(loginLog.getJti(), LocalDateTime.now()));
            loginLog.setJti(jti);
            loginLogRepository.save(loginLog);
            log.info("User '{}' refreshed token successfully. New JTI: {}", user.getUsername(), jti);
            return RefreshTokenResponse.builder()
                    .token(newToken)
                    .build();
        } catch (Exception e) {
            log.error("Error while refreshing token", e);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void checkUserLocked(User user) {
        if (Boolean.TRUE.equals(user.getIsLocked())) {
            log.info("User {} is locked", user.getUsername());
            if (user.getLockedAt() != null &&
                    user.getLockedAt().isBefore(LocalDateTime.now().minusMinutes(lockedTime))) {
                log.info("Unlocking user {} after lock time expired", user.getUsername());
                user.setIsLocked(false);
                user.setLockedAt(null);
                userRepository.save(user);
            } else {
                assert user.getLockedAt() != null;
                log.info("User {} is still locked until {}", user.getUsername(), user.getLockedAt()
                        .plusMinutes(lockedTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                String lockedUntil = user.getLockedAt()
                        .plusMinutes(lockedTime)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                throw new AppException(ErrorCode.USER_LOCKED,
                        String.format(ErrorCode.USER_LOCKED.getMessage(), lockedUntil));
            }
        }
    }

    public boolean isNConsecutiveFailedLoginAttempts(User user, int numberOfFailedLoginAttempts) {
        var lastNLoginLogs = profileService.getLoginHistory(
                FilterLoginLog.builder()
                        .userId(user.getId())
                        .page(0)
                        .size(numberOfFailedLoginAttempts)
                        .sortBy(LoginLog.Fields.createdAt)
                        .sortDirection("desc")
                        .build()
        ).getContent();


        if (lastNLoginLogs.size() < 5) {
            return false;
        }
        boolean allFailed = lastNLoginLogs.stream().noneMatch(LoginLogResponse::getSuccess);
        if (!allFailed) {
            return false;
        }
        LocalDateTime lastAttemptTime = lastNLoginLogs.getFirst().getCreatedAt();
        return lastAttemptTime.isAfter(LocalDateTime.now().minusMinutes(lockedTime));
    }

    private String generateToken(User user, String jti) {
        securityUtils.checkUserDeleted(user);

        RSAPrivateKey privateKey = securityUtils.loadPrivateKey();

        JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("namnguyen1409.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(expiredTime, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(jti)
                .claim("scope", buildScope(user))
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        JWSSigner signer = new RSASSASigner(privateKey);
        try {
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Error while signing JWT", e);
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName().name());
                role.getPermissions().forEach(permission -> {
                    if (user.getRevokedPermissions() != null && user.getRevokedPermissions().contains(permission))
                        return;
                    stringJoiner.add(permission.getName().name());
                });
            });
        }
        return stringJoiner.toString();
    }

    private void buildLoginLog(LoginLog loginLog, User user) {
        loginLog.setUser(user);
        String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpServletRequest.getRemoteAddr();
        }
        loginLog.setIpAddress(ipAddress);
        Parser parser = new Parser();
        Client client = parser.parse(httpServletRequest.getHeader("User-Agent"));
        loginLog.setUserAgent(httpServletRequest.getHeader("User-Agent"));
        loginLog.setOs(getString(client.os.family));
        loginLog.setOsVersion(getString(client.os.major));
        loginLog.setBrowser(getString(client.userAgent.family));
        loginLog.setBrowserVersion(getString(client.userAgent.major));
        loginLog.setDevice(getString(client.device.family));
    }

    private String getString(String data) {
        if (data == null || data.isEmpty()) {
            return "unknown";
        }
        return data;
    }

}
