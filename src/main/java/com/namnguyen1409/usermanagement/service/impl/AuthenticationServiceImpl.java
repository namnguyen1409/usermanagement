package com.namnguyen1409.usermanagement.service.impl;

import com.namnguyen1409.usermanagement.dto.request.*;
import com.namnguyen1409.usermanagement.dto.response.CreateUserResponse;
import com.namnguyen1409.usermanagement.dto.response.IntrospectResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginResponse;
import com.namnguyen1409.usermanagement.dto.response.RefreshTokenResponse;
import com.namnguyen1409.usermanagement.entity.LoginLog;
import com.namnguyen1409.usermanagement.entity.TokenBlacklist;
import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.enums.UserRole;
import com.namnguyen1409.usermanagement.exception.AppException;
import com.namnguyen1409.usermanagement.exception.ErrorCode;
import com.namnguyen1409.usermanagement.mapper.UserMapper;
import com.namnguyen1409.usermanagement.repository.LoginLogRepository;
import com.namnguyen1409.usermanagement.repository.RoleRepository;
import com.namnguyen1409.usermanagement.repository.TokenBlacklistRepository;
import com.namnguyen1409.usermanagement.repository.UserRepository;
import com.namnguyen1409.usermanagement.service.AuthenticationService;
import com.namnguyen1409.usermanagement.utils.SecurityUtils;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua_parser.Client;
import ua_parser.Parser;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
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

    @NonFinal
    @Value("${jwt.secret-key}")
    String SECRET_KEY;

    @NonFinal
    @Value("${jwt.expiration-time}")
    long EXPIRATION_TIME;

    @NonFinal
    @Value("${jwt.refresh-time}")
    long REFRESH_TIME;



    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            var token = request.getToken();
            boolean isValid = true;
            try {
                verifyToken(token, false);
            } catch (AppException e) {
                isValid = false;
            }
            return IntrospectResponse.builder().valid(isValid).build();
        } catch (JOSEException | ParseException e) {
            log.error("Error while introspecting token", e);
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }

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
    public LoginResponse login(LoginRequest request, HttpServletRequest httpServletRequest) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        securityUtils.checkUserDeleted(user);
        securityUtils.checkUserLocked(user);

        LoginLog loginLog = new LoginLog();
        buildLoginLog(loginLog, user, httpServletRequest);
        log.info("Login log: {}", loginLog);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginLog.setSuccess(false);
            loginLogRepository.save(loginLog);
            if (securityUtils.is5ConsecutiveFailedLoginAttempts(user)) {
                user.setIsLocked(true);
                user.setLockedAt(LocalDateTime.now());
                userRepository.save(user);
                throw new AppException(ErrorCode.USER_LOCKED);
            }
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        loginLog.setSuccess(true);
        loginLogRepository.save(loginLog);
        String token = generateToken(user);
        return LoginResponse.builder().token(token).isAuthenticated(true).build();
    }

    @Override
    public void logout(LogoutRequest request) {
        var token = request.getToken();
        try {
            SignedJWT signedJWT = verifyToken(token, false);
            tokenBlacklistRepository.save(TokenBlacklist.builder().
                    tokenId(signedJWT.getJWTClaimsSet().getJWTID())
                    .build());
        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest token) {
        try {
            SignedJWT signedJWT = verifyToken(token.getToken(), true);
            String newToken = generateToken(userRepository
                    .findByIdAndIsDeletedFalse(signedJWT.getJWTClaimsSet().getSubject())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
            tokenBlacklistRepository.save(TokenBlacklist.builder()
                    .tokenId(signedJWT.getJWTClaimsSet().getJWTID())
                    .build());
            return RefreshTokenResponse.builder().token(newToken).build();
        } catch (JOSEException | ParseException e) {
            log.error("Error while refreshing token", e);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * Sinh mã JWT cho người dùng được cung cấp, bao gồm việc thiết lập
     * thông tin tiêu đề (header) và nội dung (claims).
     *
     * @param user Đối tượng người dùng cho phép tạo mã thông báo.
     * @return Một chuỗi chứa mã thông báo JWT đã ký và được tuần tự hóa.
     * @throws RuntimeException Nếu xảy ra lỗi khi ký hoặc tạo mã thông báo.
     */
    private String generateToken(User user) {
        securityUtils.checkUserDeleted(user);
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("namnguyen1409.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(EXPIRATION_TIME, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Xác minh tính hợp lệ của mã JWT và trả về đối tượng SignedJWT nếu mã hợp lệ.
     *
     * @param token Chuỗi mã JWT cần được xác minh.
     * @param isRefreshToken Xác định mã có phải là mã refresh hay không.
     *                        - Nếu là mã refresh, thời gian hết hạn được tính dựa trên thời điểm phát hành và thời gian refresh.
     *                        - Nếu không, thời gian hết hạn sẽ lấy từ thuộc tính "expirationTime" trong mã.
     * @return Đối tượng {@link SignedJWT} sau khi xác minh nếu mã hợp lệ.
     * @throws JOSEException Nếu có lỗi xảy ra khi xác minh chữ ký của mã.
     * @throws ParseException Nếu có lỗi xảy ra khi phân tích cú pháp mã JWT.
     * @throws AppException Nếu mã không hợp lệ, đã hết hạn, hoặc đã bị đưa vào danh sách đen.
     */
    private SignedJWT verifyToken(String token, boolean isRefreshToken) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
        Date expirationTime = isRefreshToken ?
                new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESH_TIME, ChronoUnit.SECONDS).toEpochMilli()) :
                signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if (!(verified && expirationTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHORIZED);
        if (tokenBlacklistRepository.existsByTokenId(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return signedJWT;
    }

    /**
     * Tạo chuỗi scope từ thông tin người dùng bao gồm vai trò và các quyền,
     * bỏ qua các quyền đã bị thu hồi của người dùng.
     *
     * @param user Thông tin người dùng bao gồm danh sách vai trò và các quyền liên quan.
     * @return Chuỗi scope được xây dựng từ vai trò và quyền hợp lệ của người dùng.
     */
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
