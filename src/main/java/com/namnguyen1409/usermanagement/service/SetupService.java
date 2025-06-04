package com.namnguyen1409.usermanagement.service;


public interface SetupService {
    void setupRolesAndPermissions();

    void setupAdminAccount();

    void setUpTestAccount(int numberOfAccounts);

    void setupCache();

}
