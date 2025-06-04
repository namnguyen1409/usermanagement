package com.namnguyen1409.usermanagement.service.cache;

public interface UserCacheService {
    void addUsername(String username);

    Boolean isUsernameExists(String username);

    void deleteUsername(String username);

    void addEmail(String email);

    Boolean isEmailExists(String email);

    void deleteEmail(String email);

    void addPhone(String phone);

    Boolean isPhoneExists(String phone);

    void deletePhone(String phone);
}
