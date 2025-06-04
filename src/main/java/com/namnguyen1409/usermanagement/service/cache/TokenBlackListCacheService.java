package com.namnguyen1409.usermanagement.service.cache;

public interface TokenBlackListCacheService {
    void addTokenToBlackList(String token);

    Boolean isTokenInBlackList(String token);

    void syncDatabaseToCache();
}
