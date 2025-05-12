package com.namnguyen1409.usermanagement.scheduler;


import com.namnguyen1409.usermanagement.repository.LoginLogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginLogScheduler {

    LoginLogRepository loginLogRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void autoLogout() {
        int updatedCount = loginLogRepository.markExpiredSessionsAsLoggedOut(LocalDateTime.now());
        log.info("Updated {} expired sessions", updatedCount);
    }
}
