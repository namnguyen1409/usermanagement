package com.namnguyen1409.usermanagement.service;

import com.namnguyen1409.usermanagement.dto.request.CreateUserRequest;
import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.FilterUserRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponseDetail;
import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.exception.AppException;
import org.springframework.data.domain.Page;


public interface UserService {
    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(String userId, UpdateUserRequest updateUserRequest);

    void deleteUser(String userId);

    UserResponseDetail getUserById(String userId);


    Page<UserResponse> filterUsers(FilterUserRequest filterUserRequest);

    void restoreUser(String id);

    Page<LoginLogResponse> getLoginHistory(String id, FilterLoginLog filterLoginLog);

    void unlockUser(String id);

    User findByUserId(String userId) throws AppException;

    void checkUserConflict(UpdateUserRequest updateUserRequest, String id);

    User getCurrentUserIfExists();

    User getCurrentUser();
}
