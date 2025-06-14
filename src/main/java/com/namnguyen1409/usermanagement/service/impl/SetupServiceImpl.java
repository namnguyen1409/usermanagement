package com.namnguyen1409.usermanagement.service.impl;

import com.namnguyen1409.usermanagement.constants.enums.UserPermission;
import com.namnguyen1409.usermanagement.constants.enums.UserRole;
import com.namnguyen1409.usermanagement.dto.RefreshTokenInfo;
import com.namnguyen1409.usermanagement.entity.Permission;
import com.namnguyen1409.usermanagement.entity.Role;
import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.repository.*;
import com.namnguyen1409.usermanagement.service.SetupService;
import com.namnguyen1409.usermanagement.service.cache.RefreshTokenCacheService;
import com.namnguyen1409.usermanagement.service.cache.TokenBlackListCacheService;
import com.namnguyen1409.usermanagement.service.cache.UserCacheService;
import com.namnguyen1409.usermanagement.utils.RandomUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SetupServiceImpl implements SetupService {

    PermissionRepository permissionRepository;
    RoleRepository roleRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RandomUtils randomUtils;
    TokenBlacklistRepository tokenBlacklistRepository;
    UserCacheService userCacheService;
    TokenBlackListCacheService tokenBlackListCacheService;
    RefreshTokenRepository refreshTokenRepository;
    RefreshTokenCacheService refreshTokenCacheService;

    @NonFinal
    @Value("${setup.init-password}")
    String initPassword;

    @NonFinal
    @Value("${setup.admin-username}")
    String adminUsername;

    @NonFinal
    @Value("${setup.admin-password}")
    String adminPassword;


    @Transactional
    @Override
    public void setupRolesAndPermissions() {
        Map<UserPermission, Permission> existingPermissions = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(Permission::getName, permission -> permission));

        for (UserRole role : UserRole.values()) {
            Role currentRole = roleRepository.findByName(role);
            if (currentRole == null) {
                log.info("Creating role: {} - {}", role.name(), role.getDescription());
                currentRole = new Role();
                currentRole.setName(role);
                currentRole.setDescription(role.getDescription());
            } else {
                log.info("Role existed : {} - {}", role.name(), role.getDescription());
            }
            for (UserPermission permission : UserPermission.values()) {
                if (permission.getUserRole() == role) {
                    Permission currentPermission = existingPermissions.get(permission);
                    if (currentPermission == null) {
                        log.info("Creating permission: {} - {}", permission.name(), permission.getDescription());
                        currentPermission = new Permission();
                        currentPermission.setName(permission);
                        currentPermission.setDescription(permission.getDescription());
                        permissionRepository.save(currentPermission);
                    } else {
                        log.info("Permission existed : {} - {}", permission.name(), permission.getDescription());
                    }
                    currentRole.getPermissions().add(currentPermission);
                }
            }
            roleRepository.save(currentRole);
        }
    }


    @Transactional
    @Override
    public void setupAdminAccount() {
        if (userRepository.existsByUsername(adminUsername)) {
            return;
        }
        User adminUser = new User();
        adminUser.setUsername(adminUsername);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setFirstName("Nam");
        adminUser.setLastName("Nguyen");
        adminUser.setEmail("admin@namnguyen1409.com");
        adminUser.setPhone("0123456789");
        adminUser.setGender(true);
        adminUser.setBirthday(LocalDate.of(2004, 9, 14));
        adminUser.setAddress("1234 Ha Noi");
        adminUser.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(adminUser);

    }

    @Transactional
    @Override
    public void setUpTestAccount(int numberOfAccounts) {
        long existingUserCount = userRepository.count();
        if (existingUserCount >= numberOfAccounts) {
            return;
        }
        int additionalAccounts = numberOfAccounts - (int) existingUserCount;

        for (int i = 1; i < additionalAccounts; i++) {
            User user = new User();
            user.setUsername(randomUtils.generateRandomUsername());
            user.setPassword(passwordEncoder.encode(initPassword));
            user.setFirstName(randomUtils.generateRandomFirstName());
            user.setLastName(randomUtils.generateRandomLastName());
            user.setEmail(randomUtils.generateRandomEmail(user.getUsername()));
            user.setPhone(randomUtils.generateRandomPhone());
            user.setGender(randomUtils.generateRandomBoolean());
            user.setBirthday(randomUtils.generateRandomBirthday());
            user.setAddress(randomUtils.generateRandomAddress());
            if (Boolean.TRUE.equals(randomUtils.generateRandomBoolean())) {
                user.setRoles(Set.of(roleRepository.findByName(UserRole.USER)));
            } else {
                user.setRoles(Set.of(roleRepository.findByName(UserRole.USER), roleRepository.findByName(UserRole.ADMIN)));
            }
            try {
                log.info("Creating user: {}", user.getUsername());
                userRepository.save(user);
            } catch (Exception exception) {
                log.error("Error while saving user: {}", user.getUsername(), exception);
            }
        }
    }

    @Override
    public void setupCache() {
        // init the username, email, phone for cache
        if (userRepository.count() == 0) {
            log.info("No users found, skipping adding to Redis cache");
        } else {
            try {
                log.info("Setting up Redis cache for users");
                userRepository.findAll().forEach(user -> {
                    String username = user.getUsername();
                    String email = user.getEmail();
                    String phone = user.getPhone();
                    userCacheService.addUsername(username);
                    userCacheService.addEmail(email);
                    userCacheService.addPhone(phone);
                });
                log.info("Redis cache setup completed for users");
            } catch (Exception e) {
                log.error("Error while setting up Redis cache for users", e);
            }
        }

        // init the revoke token for cache
        if (tokenBlacklistRepository.count() == 0) {
            log.info("No revoked tokens found, skipping Redis setup for revoked tokens");
        } else {
            try {
                log.info("Setting up Redis cache for revoked tokens");
                tokenBlacklistRepository.findAll().forEach(tokenBlacklist -> {
                    String jti = tokenBlacklist.getTokenId();
                    tokenBlackListCacheService.addTokenToBlackList(jti);
                });
                log.info("Redis cache setup completed for revoked tokens");
            } catch (Exception e) {
                log.error("Error while setting up Redis cache for revoked tokens");
            }
        }
        // init the refresh token for cache
        if (refreshTokenRepository.count() == 0) {
            log.info("No refresh tokens found, skipping Redis setup for refresh tokens");
        } else {
            try {
                log.info("Setting up Redis cache for refresh tokens");
                refreshTokenRepository.findAll().forEach(
                        refreshToken -> refreshTokenCacheService.addRefreshToken(
                                refreshToken.getToken(),
                                new RefreshTokenInfo(
                                        refreshToken.getUser().getId(),
                                        refreshToken.getLoginLog().getId(),
                                        refreshToken.getExpiresAt()
                                )
                        ));
                log.info("Redis cache setup completed for refresh tokens");
            } catch (Exception e) {
                log.error("Error while setting up Redis cache for refresh tokens", e);
            }
        }
    }


}
