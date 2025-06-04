package com.namnguyen1409.usermanagement.service.cache;

import com.namnguyen1409.usermanagement.dto.RefreshTokenInfo;

public interface RefreshTokenCacheService {
    void addRefreshToken(String refreshToken, RefreshTokenInfo refreshTokenInfo);

    RefreshTokenInfo getRefreshToken(String refreshToken);

    void deleteRefreshToken(String refreshToken);
}
