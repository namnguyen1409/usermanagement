package com.namnguyen1409.usermanagement.scheduler;

import com.namnguyen1409.usermanagement.repository.TokenBlacklistRepository;
import com.namnguyen1409.usermanagement.service.cache.TokenBlackListCacheService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenBlackListScheduler {

    TokenBlacklistRepository tokenBlacklistRepository;
    private final TokenBlackListCacheService tokenBlackListCacheService;

    @NonFinal
    @Value("${jwt.expiration-time}")
    long expirationTime;

    @Transactional
    @Scheduled(cron = "0 */30 * * * *")
    public void removeExpiredTokens() {
        log.info("Removing expired tokens");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime exp = now.minusSeconds(expirationTime);
        tokenBlacklistRepository.deleteExpiredTokens(exp);

        // update cache in redis
        tokenBlackListCacheService.syncDatabaseToCache();
    }

}
