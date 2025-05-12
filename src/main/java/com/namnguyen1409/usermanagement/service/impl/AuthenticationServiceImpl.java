package com.namnguyen1409.usermanagement.service.impl;

import com.namnguyen1409.usermanagement.dto.request.*;
import com.namnguyen1409.usermanagement.dto.response.CreateUserResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginResponse;
import com.namnguyen1409.usermanagement.dto.response.RefreshTokenResponse;
import com.namnguyen1409.usermanagement.entity.LoginLog;
import com.namnguyen1409.usermanagement.entity.RefreshToken;
import com.namnguyen1409.usermanagement.entity.TokenBlacklist;
import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.enums.UserRole;
import com.namnguyen1409.usermanagement.exception.AppException;
import com.namnguyen1409.usermanagement.exception.ErrorCode;
import com.namnguyen1409.usermanagement.mapper.UserMapper;
import com.namnguyen1409.usermanagement.repository.*;
import com.namnguyen1409.usermanagement.service.AuthenticationService;
import com.namnguyen1409.usermanagement.utils.SecurityUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
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
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    UserRepository userRepository;
    TokenBlacklistRepository tokenBlacklistRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    RoleRepository roleRepository;
    SecurityUtils securityUtils;
    LoginLogRepository loginLogRepository;
    RefreshTokenRepository refreshTokenRepository;


    @NonFinal
    NimbusJwtDecoder nimbusJwtDecoder;

    @NonFinal
    @Value("${jwt.expiration-time}")
    long EXPIRATION_TIME;

    @NonFinal
    @Value("${jwt.refresh-time}")
    long REFRESH_TIME;

    @NonFinal
    @Value("${user.locked-time}")
    long LOCKED_TIME;


    @Override
    public CreateUserResponse register(CreateUserRequest request) {
        User user = userMapper.toUser(request);
        user.setIsDeleted(false);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(roleRepository.findByName(UserRole.USER)));
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return CreateUserResponse.builder().success(true).build();
    }

    @Transactional(noRollbackFor = {AppException.class})
    @Override
    public LoginResponse login(LoginRequest request,
                               HttpServletRequest httpServletRequest,
                               HttpServletResponse httpServletResponse
    ) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        securityUtils.checkUserDeleted(user);
        securityUtils.checkUserLocked(user);
        LoginLog loginLog = new LoginLog();
        buildLoginLog(loginLog, user, httpServletRequest);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginLog.setLogout(true);
            loginLog.setSuccess(false);
            loginLogRepository.save(loginLog);
            if (securityUtils.is5ConsecutiveFailedLoginAttempts(user)) {
                user.setIsLocked(true);
                user.setLockedAt(LocalDateTime.now());
                userRepository.save(user);

                String lockedUntil = user.getLockedAt()
                        .plusMinutes(LOCKED_TIME)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                throw new AppException(ErrorCode.USER_LOCKED,
                        String.format(ErrorCode.USER_LOCKED.getMessage(), lockedUntil));
            }
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        loginLog.setSuccess(true);
        String jti = UUID.randomUUID().toString();
        String token = generateToken(user, jti);
        loginLog.setJti(jti);
        String refreshToken = null;
        if (request.getRememberMe()) {
            refreshToken = UUID.randomUUID().toString();
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .user(user)
                    .token(securityUtils.hashRefreshToken(refreshToken))
                    .loginLog(loginLog)
                    .expiresAt(LocalDateTime.now().plusSeconds(REFRESH_TIME))
                    .revoked(false)
                    .build();
            loginLog.setRefreshToken(refreshTokenEntity);
            loginLog.setExpiredAt(LocalDateTime.now().plusSeconds(REFRESH_TIME));
            securityUtils.setRefreshTokenCookie(httpServletResponse, refreshToken);
        } else {
            loginLog.setExpiredAt(LocalDateTime.now().plusSeconds(EXPIRATION_TIME));
        }
        loginLogRepository.save(loginLog);
        return LoginResponse
                .builder()
                .token(token)
                .refreshToken(refreshToken)
                .loginLogId(loginLog.getId())
                .isAuthenticated(true).build();
    }

    @Override
    public void logout(LogoutRequest request, HttpServletResponse httpServletResponse) {
        var token = request.getToken();
        try {
            if (Objects.isNull(nimbusJwtDecoder)) {
                nimbusJwtDecoder = NimbusJwtDecoder.withPublicKey(securityUtils.loadPublicKey()).build();
            }
            Jwt jwt = nimbusJwtDecoder.decode(token);
            String jti = jwt.getClaims().get("jti").toString();
            LocalDateTime exp = LocalDateTime.parse(
                    jwt.getClaims().get("exp").toString(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            );
            log.info("jti: {}", jti);
            var existsByTokenId = tokenBlacklistRepository.existsByTokenId(jti);
            if (existsByTokenId) {
                log.error("Token is already blacklisted");
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
            var tokenBlacklist = TokenBlacklist.builder()
                    .tokenId(jti)
                    .expiredAt(exp)
                    .build();
            var loginLog = loginLogRepository.findByJti(jti)
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

            loginLog.setLogout(true);

            if (loginLog.getRefreshToken() != null) {
                var refreshToken = loginLog.getRefreshToken();
                refreshToken.setRevoked(true);
                loginLog.setRefreshToken(refreshToken);
            }
            loginLogRepository.save(loginLog);
            tokenBlacklistRepository.save(tokenBlacklist);
            securityUtils.clearRefreshTokenCookie(httpServletResponse);
        } catch (Exception e) {
            log.error("Error while logging out", e);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Transactional
    @Override
    public RefreshTokenResponse refreshToken(HttpServletRequest httpServletRequest) {
        try {
            String token = securityUtils.getRefreshTokenFromCookie(httpServletRequest);
            RefreshToken refreshToken = refreshTokenRepository.findByToken(securityUtils.hashRefreshToken(token))
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
            if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }
            if (refreshToken.getRevoked()) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
            var user = refreshToken.getUser();
            securityUtils.checkUserDeleted(user);
            String jti = UUID.randomUUID().toString();
            String newToken = generateToken(user, jti);
            var loginLog = refreshToken.getLoginLog();
            tokenBlacklistRepository.save(new TokenBlacklist(loginLog.getJti(), LocalDateTime.now()));
            loginLog.setJti(jti);
            loginLogRepository.save(loginLog);
            return RefreshTokenResponse.builder()
                    .token(newToken)
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public void introspect(IntrospectRequest introspectRequest) {
        if (introspectRequest.getJti() == null || introspectRequest.getJti().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        var existsByTokenId = tokenBlacklistRepository.existsByTokenId(introspectRequest.getJti());
        if (existsByTokenId) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
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
                        Instant.now().plus(EXPIRATION_TIME, ChronoUnit.SECONDS).toEpochMilli()
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

    private void buildLoginLog(LoginLog loginLog, User user, HttpServletRequest request) {
        loginLog.setUser(user);
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        loginLog.setIpAddress(ipAddress);
        Parser parser = new Parser();
        Client client = parser.parse(request.getHeader("User-Agent"));
        loginLog.setUserAgent(request.getHeader("User-Agent"));
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
