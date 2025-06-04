package com.namnguyen1409.usermanagement.service.impl;

import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserPasswordRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import com.namnguyen1409.usermanagement.entity.TokenBlacklist;
import com.namnguyen1409.usermanagement.exception.AppException;
import com.namnguyen1409.usermanagement.exception.ErrorCode;
import com.namnguyen1409.usermanagement.mapper.UserMapper;
import com.namnguyen1409.usermanagement.repository.LoginLogRepository;
import com.namnguyen1409.usermanagement.repository.TokenBlacklistRepository;
import com.namnguyen1409.usermanagement.repository.UserRepository;
import com.namnguyen1409.usermanagement.service.LoginLogService;
import com.namnguyen1409.usermanagement.service.ProfileService;
import com.namnguyen1409.usermanagement.service.UserService;
import com.namnguyen1409.usermanagement.utils.LogUtils;
import com.namnguyen1409.usermanagement.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileServiceImpl implements ProfileService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    LoginLogRepository loginLogRepository;
    TokenBlacklistRepository tokenBlacklistRepository;
    LogUtils logUtils;
    UserService userService;
    LoginLogService loginLogService;
    SecurityUtils securityUtils;

    @Override
    public UserResponse view() {
        return userMapper.toUserResponse(userService.getCurrentUser());
    }

    @Override
    public UserResponse update(UpdateUserRequest updateUserRequest) {
        log.info("Updating user: {}", logUtils.logObject(updateUserRequest,
                UpdateUserRequest.Fields.email));
        var user = userService.getCurrentUser();
        userMapper.updateUser(user, updateUserRequest);
        userService.checkUserConflict(updateUserRequest, user.getId());
        try {
            user = userRepository.save(user);
        } catch (Exception exception) {
            log.error("Error while updating user", exception);
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    public void updatePassword(UpdateUserPasswordRequest request) {
        var user = userService.getCurrentUser();
        log.info("Updating password for user: {}", user.getUsername());
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Invalid old password for user: {}", user.getUsername());
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        try {
            userRepository.save(user);
            log.info("Password updated successfully for user: {}", user.getUsername());
        } catch (Exception exception) {
            log.error("Error while updating password", exception);
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }

    @PreAuthorize("!hasRole('SUPER_ADMIN')")
    @Override
    public void delete() {
        var user = userService.getCurrentUser();
        log.info("Deleting user: {}", user.getUsername());
        user.markAsDeleted(user.getId());
        try {
            userRepository.save(user);
            log.info("User deleted successfully: {}", user.getUsername());
        } catch (Exception exception) {
            log.error("Error while deleting user", exception);
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }

    @Override
    public Page<LoginLogResponse> getLoginHistory(FilterLoginLog filterRequest) {
        log.info("Fetching login history for user: {} with filter: {}",
                userService.getCurrentUser().getUsername(),
                logUtils.logObject(filterRequest)
        );
        return loginLogService.getLoginLogResponses(filterRequest, userService.getCurrentUser());
    }

    @Transactional
    @Override
    public void revokeLoginLog(String loginLogId) {
        log.info("Revoking login log with ID: {}", loginLogId);
        var user = userService.getCurrentUser();
        var loginLog = loginLogRepository.findById(loginLogId)
                .orElseThrow(() -> new AppException(ErrorCode.LOGIN_LOG_NOT_FOUND));

        if (!loginLog.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized attempt to revoke login log with ID: {} by user: {}", loginLogId, user.getUsername());
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        loginLog.setLogout(true);
        securityUtils.revokeRefreshTokenIfExists(loginLog);
        tokenBlacklistRepository.save(new TokenBlacklist(loginLog.getJti(), loginLog.getExpiredAt()));
        log.info("Login log with ID: {} revoked successfully for user: {}", loginLogId, user.getUsername());
    }

}
