package com.namnguyen1409.usermanagement.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, String> {
    List<LoginLog> findTop5ByUserOrderByCreatedAtDesc(User user);
}