package com.namnguyen1409.usermanagement.service;

import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserPasswordRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProfileService {

    UserResponse view();

    UserResponse update(UpdateUserRequest updateUserRequest);

    void updatePassword(UpdateUserPasswordRequest request);

    void delete();

    Page<LoginLogResponse> getLoginHistory(FilterLoginLog filterRequest);
}
