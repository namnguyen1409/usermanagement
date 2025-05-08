package com.namnguyen1409.usermanagement.utils;

import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.entity.LoginLog;
import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.exception.AppException;
import com.namnguyen1409.usermanagement.exception.ErrorCode;
import com.namnguyen1409.usermanagement.mapper.LoginLogMapper;
import com.namnguyen1409.usermanagement.repository.LoginLogRepository;
import com.namnguyen1409.usermanagement.repository.UserRepository;
import com.namnguyen1409.usermanagement.specification.LoginLogSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityUtils {

    UserRepository userRepository;
    LoginLogRepository loginLogRepository;
    LoginLogMapper loginLogMapper;


    @NonFinal
    @Value("${user.locked-time}")
    long LOCKED_TIME;

    @NonFinal
    @Value("${jwt.private-key-location}")
    Resource privateKeyLocation;

    @NonFinal
    @Value("${jwt.public-key-location}")
    Resource publicKeyLocation;

    public RSAPrivateKey loadPrivateKey() {
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
        try (InputStream inputStream = publicKeyLocation.getInputStream()) {
            String pem = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .filter(line -> !line.startsWith("-----"))
                    .collect(Collectors.joining());

            byte[] keyBytes = Base64.getDecoder().decode(pem);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }

    public String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public User getCurrentUser() {
        String userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public void checkUserConflict(UpdateUserRequest request, String userId) {
        if (request.getUsername() != null && userRepository.existsByUsernameAndIdNot(request.getUsername(), userId)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getEmail() != null && userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getPhone() != null && userRepository.existsByPhoneAndIdNot(request.getPhone(), userId)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }

    public void checkUserDeleted(User user) {
        if (user.getIsDeleted()) {
            throw new AppException(ErrorCode.USER_DELETED);
        }
    }

    public void checkUserLocked(User user) {
        if (Boolean.TRUE.equals(user.getIsLocked())) {
            if (user.getLockedAt() != null &&
                    user.getLockedAt().isBefore(LocalDateTime.now().minusMinutes(LOCKED_TIME))) {
                user.setIsLocked(false);
                user.setLockedAt(null);
                userRepository.save(user);
            } else {
                assert user.getLockedAt() != null;
                String lockedUntil = user.getLockedAt()
                        .plusMinutes(LOCKED_TIME)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                throw new AppException(ErrorCode.USER_LOCKED,
                        String.format(ErrorCode.USER_LOCKED.getMessage(), lockedUntil));
            }
        }
    }


    public boolean is5ConsecutiveFailedLoginAttempts(User user) {
        List<LoginLog> last5LoginLogs = loginLogRepository.findTop5ByUserOrderByCreatedAtDesc(user);
        if (last5LoginLogs.size() < 5) {
            return false;
        }
        boolean allFailed = last5LoginLogs.stream().noneMatch(LoginLog::getSuccess);
        if (!allFailed) {
            return false;
        }
        LocalDateTime lastAttemptTime = last5LoginLogs.getFirst().getCreatedAt();
        return lastAttemptTime.isAfter(LocalDateTime.now().minusMinutes(LOCKED_TIME));
    }

    @NotNull
    public Page<LoginLogResponse> getLoginLogResponses(FilterLoginLog filterLoginLog, User user) {
        filterLoginLog.setUserId(user.getId());
        Sort sortDirection = "asc".equalsIgnoreCase(filterLoginLog.getSortDirection())
                ? Sort.by(filterLoginLog.getSortBy()).ascending()
                : Sort.by(filterLoginLog.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filterLoginLog.getPage(), filterLoginLog.getSize(), sortDirection);
        var spec = LoginLogSpecification.buildSpecification(filterLoginLog);
        return loginLogRepository.findAll(spec, pageable).map(loginLogMapper::toLoginLogResponse);
    }

    public boolean isSuperAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    public void checkAdminPrivileges(User targetUser) {
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

}
