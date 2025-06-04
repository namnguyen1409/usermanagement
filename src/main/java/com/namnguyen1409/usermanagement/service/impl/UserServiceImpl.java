package com.namnguyen1409.usermanagement.service.impl;

import com.namnguyen1409.usermanagement.constants.enums.UserRole;
import com.namnguyen1409.usermanagement.dto.request.CreateUserRequest;
import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.FilterUserRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponseDetail;
import com.namnguyen1409.usermanagement.entity.Permission;
import com.namnguyen1409.usermanagement.entity.Role;
import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.exception.AppException;
import com.namnguyen1409.usermanagement.exception.ErrorCode;
import com.namnguyen1409.usermanagement.mapper.UserMapper;
import com.namnguyen1409.usermanagement.repository.PermissionRepository;
import com.namnguyen1409.usermanagement.repository.RoleRepository;
import com.namnguyen1409.usermanagement.repository.UserRepository;
import com.namnguyen1409.usermanagement.service.LoginLogService;
import com.namnguyen1409.usermanagement.service.UserService;
import com.namnguyen1409.usermanagement.specification.UserSpecification;
import com.namnguyen1409.usermanagement.utils.LogUtils;
import com.namnguyen1409.usermanagement.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    SecurityUtils securityUtils;
    LogUtils logUtils;
    LoginLogService loginLogService;


    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with request: {}", logUtils.logObject(request, CreateUserRequest.Fields.password));
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        handleRolesAndPermissions(request.getRoleList(), request.getRevokedPermissionList(), user);
        return userMapper.toUserResponse(saveUser(user));
    }


    @Override
    public UserResponse updateUser(String userId, UpdateUserRequest updateUserRequest) {
        var user = userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        securityUtils.checkUpdatePermission(user);

        checkUserConflict(updateUserRequest, userId);

        userMapper.updateUser(user, updateUserRequest);
        handleRolesAndPermissions(updateUserRequest.getRoles(), updateUserRequest.getRevokedPermissions(), user);

        return userMapper.toUserResponse(saveUser(user));
    }

    @Override
    public void deleteUser(String userId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        securityUtils.checkUpdatePermission(user);
        user.markAsDeleted(securityUtils.getCurrentUserId());
        saveUser(user);
    }


    @Override
    public UserResponseDetail getUserById(String userId) {
        return userMapper.toUserResponseDetail(findByUserId(userId));
    }


    @Override
    public Page<UserResponse> filterUsers(FilterUserRequest filterUserRequest) {
        Sort sortDirection = "asc".equalsIgnoreCase(filterUserRequest.getSortDirection())
                ? Sort.by(filterUserRequest.getSortBy()).ascending()
                : Sort.by(filterUserRequest.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filterUserRequest.getPage(), filterUserRequest.getSize(), sortDirection);
        var spec = UserSpecification.buildSpecification(filterUserRequest);
        return userRepository.findAll(spec, pageable).map(userMapper::toUserResponse);
    }

    @Override
    public void restoreUser(String id) {
        log.info("Restoring user with id: {}", id);
        var user = findByUserId(id);
        if (Boolean.FALSE.equals(user.getIsDeleted())) {
            throw new AppException(ErrorCode.USER_NOT_DELETED);
        }
        user.restore();
        saveUser(user);
    }

    @Override
    public Page<LoginLogResponse> getLoginHistory(String id, FilterLoginLog filterLoginLog) {
        return loginLogService.getLoginLogResponses(filterLoginLog, findByUserId(id));
    }

    @Override
    public void unlockUser(String id) {
        var user = findByUserId(id);
        log.info("Unlocking user with id: {}", id);
        securityUtils.checkUpdatePermission(user);
        if (Boolean.FALSE.equals(user.getIsLocked())) {
            throw new AppException(ErrorCode.USER_NOT_LOCKED);
        }
        user.setIsLocked(false);
        user.setLockedAt(null);
        saveUser(user);
    }

    @Override
    public User findByUserId(String userId) throws AppException {
        return userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
    }

    @Override
    public User getCurrentUserIfExists() throws AppException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        String userId = getCurrentUserId();
        return findByUserId(userId);
    }

    public String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public User getCurrentUser() throws AppException {
        String userId = getCurrentUserId();
        return findByUserId(userId);
    }

    private void handleRolesAndPermissions(Set<String> roleList, Set<String> revokedPermissionList, User user) {
        boolean superAdmin = securityUtils.isSuperAdmin();

        if (roleList != null && superAdmin) {
            Set<UserRole> userRoles = roleList.stream()
                    .map(String::toUpperCase)
                    .filter(role -> !role.equals("SUPER_ADMIN"))
                    .map(UserRole::valueOf)
                    .collect(Collectors.toSet());
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(userRoles));
            user.setRoles(roles);
        }

        if (revokedPermissionList != null && superAdmin) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(revokedPermissionList));
            user.setRevokedPermissions(permissions);
        }

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>(roleRepository.findAllById(Set.of(UserRole.USER))));
        }
    }


    private User saveUser(User user) {
        try {
            user = userRepository.save(user);
        } catch (Exception exception) {
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
        return user;
    }

    public void checkUserConflict(UpdateUserRequest request, String userId) {
        if (request.getUsername() != null && userRepository.existsByUsernameAndIdNot(request.getUsername(), userId)) {
            throw new AppException(ErrorCode.USER_NAME_EXISTED);
        }
        if (request.getEmail() != null && userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
            throw new AppException(ErrorCode.USER_EMAIL_EXISTED);
        }
        if (request.getPhone() != null && userRepository.existsByPhoneAndIdNot(request.getPhone(), userId)) {
            throw new AppException(ErrorCode.USER_PHONE_EXISTED);
        }
    }


}
