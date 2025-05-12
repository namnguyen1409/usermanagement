package com.namnguyen1409.usermanagement.repository;

import com.namnguyen1409.usermanagement.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {
    boolean existsByTokenId(String tokenId);

    @Modifying
    @Query("DELETE FROM TokenBlacklist tbl WHERE tbl.expiredAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

}