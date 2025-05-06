package com.namnguyen1409.usermanagement.repository;

import com.namnguyen1409.usermanagement.entity.LoginLog;
import com.namnguyen1409.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, String>, JpaSpecificationExecutor<LoginLog> {
    List<LoginLog> findTop5ByUserOrderByCreatedAtDesc(User user);

    List<LoginLog> findAllByUserId(String id);
}