package com.namnguyen1409.usermanagement.service.impl;

import com.namnguyen1409.usermanagement.entity.Permission;
import com.namnguyen1409.usermanagement.entity.Role;
import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.enums.UserPermission;
import com.namnguyen1409.usermanagement.enums.UserRole;
import com.namnguyen1409.usermanagement.repository.PermissionRepository;
import com.namnguyen1409.usermanagement.repository.RoleRepository;
import com.namnguyen1409.usermanagement.repository.UserRepository;
import com.namnguyen1409.usermanagement.service.RoleService;
import com.namnguyen1409.usermanagement.service.SetupService;
import com.namnguyen1409.usermanagement.utils.RandomUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
    private final RoleService roleService;


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
        if(userRepository.existsByUsername("admin")) {
            return;
        }
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin"));
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
            user.setPassword(passwordEncoder.encode("123456"));
            user.setFirstName(randomUtils.generateRandomFirstName());
            user.setLastName(randomUtils.generateRandomLastName());
            user.setEmail(randomUtils.generateRandomEmail(user.getUsername()));
            user.setPhone(randomUtils.generateRandomPhone());
            user.setGender(randomUtils.generateRandomBoolean());
            user.setBirthday(randomUtils.generateRandomBirthday());
            user.setAddress(randomUtils.generateRandomAddress());
            if (randomUtils.generateRandomBoolean()) {
                user.setRoles(Set.of(roleRepository.findByName(UserRole.USER)));
            } else {
                user.setRoles(Set.of(roleRepository.findByName(UserRole.USER), roleRepository.findByName(UserRole.ADMIN)));
            }
            try{
                log.info("Creating user: {}", user.getUsername());
                userRepository.save(user);
            } catch (Exception exception) {
                log.error("Error while saving user: {}", user.getUsername(), exception);
            }
        }
    }


}
