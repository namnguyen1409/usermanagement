package com.namnguyen1409.usermanagement.service;

import org.springframework.transaction.annotation.Transactional;

public interface SetupService {
    @Transactional
    void setupRolesAndPermissions();

    @Transactional
    void setupAdminAccount();

    @Transactional
    void setUpTestAccount(int numberOfAccounts);
}
