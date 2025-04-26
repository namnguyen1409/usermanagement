package com.namnguyen1409.usermanagement.repository;

import com.namnguyen1409.usermanagement.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {
    boolean existsByTokenId(String tokenId);
}