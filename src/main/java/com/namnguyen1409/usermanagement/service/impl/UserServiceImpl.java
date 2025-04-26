package com.namnguyen1409.usermanagement.service.impl;

import com.namnguyen1409.usermanagement.dto.request.CreateUserRequest;
import com.namnguyen1409.usermanagement.dto.request.FilterUserRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponseDetail;
import com.namnguyen1409.usermanagement.entity.Permission;
import com.namnguyen1409.usermanagement.entity.Role;
import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.enums.UserRole;
import com.namnguyen1409.usermanagement.exception.AppException;
import com.namnguyen1409.usermanagement.exception.ErrorCode;
import com.namnguyen1409.usermanagement.mapper.UserMapper;
import com.namnguyen1409.usermanagement.repository.PermissionRepository;
import com.namnguyen1409.usermanagement.repository.RoleRepository;
import com.namnguyen1409.usermanagement.repository.UserRepository;
import com.namnguyen1409.usermanagement.service.UserService;
import com.namnguyen1409.usermanagement.specification.UserSpecification;
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


    @PreAuthorize("hasAnyAuthority('ADD_USER')")
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsDeleted(false);
        handleRolesAndPermissions(request.getRoleList(), request.getRevokedPermissionList(), user);
        return userMapper.toUserResponse(saveUser(user));
    }



    @PreAuthorize("hasAnyAuthority('EDIT_USER')")
    @Override
    public UserResponse updateUser(String userId, UpdateUserRequest updateUserRequest) {
        var user = securityUtils.getById(userId);

        securityUtils.checkUserDeleted(user);

        securityUtils.checkAdminPrivileges(user);

        userMapper.updateUser(user, updateUserRequest);
        handleRolesAndPermissions(updateUserRequest.getRoles(), updateUserRequest.getRevokedPermissions(), user);

        return userMapper.toUserResponse(saveUser(user));
    }

    @PreAuthorize("hasAnyAuthority('DELETE_USER')")
    @Override
    public void deleteUser(String userId) {
        var user = securityUtils.getById(userId);
        securityUtils.checkUserDeleted(user);
        securityUtils.checkAdminPrivileges(user);
        user.markAsDeleted(securityUtils.getCurrentUserId());
        saveUser(user);
    }


    @PreAuthorize("hasAnyAuthority('VIEW_USER')")
    @Override
    public UserResponseDetail getUserById(String userId) {
        return userMapper.toUserResponseDetail(securityUtils.getById(userId));
    }


    @PreAuthorize("hasAnyAuthority('VIEW_USER')")
    @Override
    public Set<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toSet());
    }

    @PreAuthorize("hasAnyAuthority('VIEW_USER')")
    @Override
    public Page<UserResponse> filterUsers(FilterUserRequest filterUserRequest) {
        Sort sortDirection = "asc".equalsIgnoreCase(filterUserRequest.getSortDirection())
                ? Sort.by(filterUserRequest.getSortBy()).ascending()
                : Sort.by(filterUserRequest.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filterUserRequest.getPage(), filterUserRequest.getSize(), sortDirection);
        var spec = UserSpecification.buildSpecification(filterUserRequest);
        return userRepository.findAll(spec, pageable).map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Override
    public void restoreUser(String id) {
        log.info("Restoring user with id: {}", id);
        var user = securityUtils.getById(id);
        if (!user.getIsDeleted()) {
            throw new AppException(ErrorCode.USER_NOT_DELETED);
        }
        user.restore();
        saveUser(user);
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

}
