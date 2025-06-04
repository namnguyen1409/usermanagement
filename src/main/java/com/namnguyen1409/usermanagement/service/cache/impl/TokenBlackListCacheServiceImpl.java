package com.namnguyen1409.usermanagement.service.cache.impl;

import com.namnguyen1409.usermanagement.constants.RedisKey;
import com.namnguyen1409.usermanagement.repository.TokenBlacklistRepository;
import com.namnguyen1409.usermanagement.service.cache.TokenBlackListCacheService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenBlackListCacheServiceImpl implements TokenBlackListCacheService {

    private final RedisTemplate<Object, Object> redisTemplate;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    public void addTokenToBlackList(String token) {
        redisTemplate.opsForSet().add(RedisKey.ACCESS_TOKEN_BLACKLIST_SET, token);
    }

    @Override
    public Boolean isTokenInBlackList(String token) {
        return redisTemplate.opsForSet().isMember(RedisKey.ACCESS_TOKEN_BLACKLIST_SET, token);
    }


    @Override
    public void syncDatabaseToCache() {
        redisTemplate.delete(RedisKey.ACCESS_TOKEN_BLACKLIST_SET);
        if (tokenBlacklistRepository.count() == 0) {
            log.info("No tokens in blacklist, cache is empty");
            return;
        }
        tokenBlacklistRepository.findAll().forEach(tokenBlacklist -> {
            redisTemplate.opsForSet().add(RedisKey.ACCESS_TOKEN_BLACKLIST_SET, tokenBlacklist.getTokenId());
        });
        log.info("Synchronized token blacklist from database to cache");
    }
}
