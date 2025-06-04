package com.namnguyen1409.usermanagement.repository;

import com.namnguyen1409.usermanagement.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, String>, JpaSpecificationExecutor<LoginLog> {

    Optional<LoginLog> findByJti(String token);

    @Modifying
    @Query("UPDATE LoginLog l SET l.logout = true WHERE l.logout = false AND l.expiredAt <= :now")
    int markExpiredSessionsAsLoggedOut(@Param("now") LocalDateTime now);

}