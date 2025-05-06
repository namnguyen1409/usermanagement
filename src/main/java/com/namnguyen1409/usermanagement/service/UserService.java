package com.namnguyen1409.usermanagement.service;

import com.namnguyen1409.usermanagement.dto.request.CreateUserRequest;
import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.FilterUserRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponseDetail;
import org.springframework.data.domain.Page;

import java.util.Set;


public interface UserService {
    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(String userId, UpdateUserRequest updateUserRequest);

    void deleteUser(String userId);

    UserResponseDetail getUserById(String userId);

    Set<UserResponse> getAllUsers();

    Page<UserResponse> filterUsers(FilterUserRequest filterUserRequest);

    void restoreUser(String id);

    Page<LoginLogResponse> getLoginHistory(String id, FilterLoginLog filterLoginLog);
}
