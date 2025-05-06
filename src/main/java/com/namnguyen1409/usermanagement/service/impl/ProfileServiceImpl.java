package com.namnguyen1409.usermanagement.service.impl;

import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserPasswordRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import com.namnguyen1409.usermanagement.exception.AppException;
import com.namnguyen1409.usermanagement.exception.ErrorCode;
import com.namnguyen1409.usermanagement.mapper.LoginLogMapper;
import com.namnguyen1409.usermanagement.mapper.UserMapper;
import com.namnguyen1409.usermanagement.repository.LoginLogRepository;
import com.namnguyen1409.usermanagement.repository.UserRepository;
import com.namnguyen1409.usermanagement.service.ProfileService;
import com.namnguyen1409.usermanagement.specification.LoginLogSpecification;
import com.namnguyen1409.usermanagement.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileServiceImpl implements ProfileService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    SecurityUtils securityUtils;
    LoginLogRepository loginLogRepository;
    LoginLogMapper loginLogMapper;

    @Override
    public UserResponse view() {
        return userMapper.toUserResponse(securityUtils.getCurrentUser());
    }

    @Override
    public UserResponse update(UpdateUserRequest updateUserRequest) {
        var user = securityUtils.getCurrentUser();
        userMapper.updateUser(user, updateUserRequest);
        securityUtils.checkUserConflict(updateUserRequest, user.getId());
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
        var user = securityUtils.getCurrentUser();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        try {
            userRepository.save(user);
        } catch (Exception exception) {
            log.error("Error while updating password", exception);
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }

    @PreAuthorize("!hasRole('SUPER_ADMIN')")
    @Override
    public void delete() {
        var user = securityUtils.getCurrentUser();
        user.markAsDeleted(user.getId());
        try {
            userRepository.save(user);
        } catch (Exception exception) {
            log.error("Error while deleting user", exception);
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }

    @Override
    public Page<LoginLogResponse> getLoginHistory(FilterLoginLog filterRequest) {
        var user = securityUtils.getCurrentUser();
        filterRequest.setUserId(user.getId());
        Sort sortDirection = "asc".equalsIgnoreCase(filterRequest.getSortDirection())
                ? Sort.by(filterRequest.getSortBy()).ascending()
                : Sort.by(filterRequest.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sortDirection);
        var spec = LoginLogSpecification.buildSpecification(filterRequest);
        return loginLogRepository.findAll(spec, pageable).map(loginLogMapper::toLoginLogResponse);
    }

}
