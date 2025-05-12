package com.namnguyen1409.usermanagement.configuration;

import com.namnguyen1409.usermanagement.service.SetupService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DataInitializer {
    SetupService setupService;

    @Transactional
    @PostConstruct
    public void init() {
        log.info("Initializing data...");
        setupService.setupRolesAndPermissions();
        setupService.setupAdminAccount();
        setupService.setUpTestAccount(50);
    }


}
