package com.namnguyen1409.usermanagement.service.cache.impl;

import com.namnguyen1409.usermanagement.constants.RedisKey;
import com.namnguyen1409.usermanagement.dto.RefreshTokenInfo;
import com.namnguyen1409.usermanagement.service.cache.RefreshTokenCacheService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenCacheServiceImpl implements RefreshTokenCacheService {

    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void addRefreshToken(String refreshToken, RefreshTokenInfo refreshTokenInfo) {
        try {
            redisTemplate.opsForValue().set(String.join(":", RedisKey.REFRESH_TOKEN_SET, refreshToken), refreshTokenInfo,
                    Duration.between(LocalDateTime.now(), refreshTokenInfo.getExpiresAt())
            );
        } catch (Exception e) {
            log.error("Failed to add refresh token to cache: {}", e.getMessage(), e);
        }
    }

    @Override
    public RefreshTokenInfo getRefreshToken(String refreshToken) {
        Object value = redisTemplate.opsForValue().get(String.join(":", RedisKey.REFRESH_TOKEN_SET, refreshToken));
        if (value instanceof RefreshTokenInfo) {
            return (RefreshTokenInfo) value;
        }
        return null;
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(String.join(":", RedisKey.REFRESH_TOKEN_SET, refreshToken));
    }

}
