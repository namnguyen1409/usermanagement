package com.namnguyen1409.usermanagement.repository;

import com.namnguyen1409.usermanagement.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
  }