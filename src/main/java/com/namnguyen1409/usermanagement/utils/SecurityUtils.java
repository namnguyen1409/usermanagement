package com.namnguyen1409.usermanagement.utils;

import com.namnguyen1409.usermanagement.entity.LoginLog;
import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.exception.AppException;
import com.namnguyen1409.usermanagement.exception.ErrorCode;
import com.namnguyen1409.usermanagement.service.cache.RefreshTokenCacheService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityUtils {


    HttpServletResponse httpServletResponse;
    HttpServletRequest httpServletRequest;
    private final RefreshTokenCacheService refreshTokenCacheService;


    @NonFinal
    @Value("${jwt.refresh-name}")
    String refreshName;

    @NonFinal
    @Value("${jwt.refresh-time}")
    long refreshTime;


    @NonFinal
    @Value("${jwt.private-key-location}")
    Resource privateKeyLocation;

    @NonFinal
    @Value("${jwt.public-key-location}")
    Resource publicKeyLocation;

    public RSAPrivateKey loadPrivateKey() {
        log.debug("Loading private key from: {}", privateKeyLocation.getFilename());
        try (InputStream inputStream = privateKeyLocation.getInputStream()) {
            String pem = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .filter(line -> !line.startsWith("-----"))
                    .collect(Collectors.joining());

            byte[] keyBytes = Base64.getDecoder().decode(pem);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("Error loading private key: {}", e.getMessage());
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }

    public RSAPublicKey loadPublicKey() {
        log.debug("Loading public key from: {}", publicKeyLocation.getFilename());
        try (InputStream inputStream = publicKeyLocation.getInputStream()) {
            String pem = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .filter(line -> !line.startsWith("-----"))
                    .collect(Collectors.joining());

            byte[] keyBytes = Base64.getDecoder().decode(pem);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            log.error("Error loading public key: {}", e.getMessage());
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }

    public String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    public void checkUserDeleted(User user) {
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            log.info("User {} is deleted", user.getUsername());
            throw new AppException(ErrorCode.USER_DELETED);
        }
    }


    public boolean isSuperAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    public void checkUpdatePermission(User targetUser) {
        boolean isTargetAdmin = targetUser.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ADMIN"));

        boolean isTargetSuperAdmin = targetUser.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("SUPER_ADMIN"));

        if (isTargetSuperAdmin) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_SUPER_ADMIN);
        }

        if (isTargetAdmin && !isSuperAdmin()) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_OTHER_ADMIN);
        }
    }


    public void setRefreshTokenCookie(String refreshToken) {
        var cookie = new Cookie(refreshName, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshTime);
        cookie.setDomain("telecomic.top");
        cookie.setAttribute("SameSite", "Lax");
        httpServletResponse.addCookie(cookie);
    }

    public void clearRefreshTokenCookie() {
        var cookie = new Cookie(refreshName, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setDomain("telecomic.top");
        cookie.setAttribute("SameSite", "Lax");
        httpServletResponse.addCookie(cookie);
    }

    public String getRefreshTokenFromCookie() {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (refreshName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void revokeRefreshTokenIfExists(LoginLog loginLog) {
        if (loginLog.getRefreshToken() != null) {
            loginLog.getRefreshToken().setRevoked(true);
            refreshTokenCacheService.deleteRefreshToken(loginLog.getRefreshToken().getToken());
        }
    }


}
