package com.namnguyen1409.usermanagement.service;

import com.namnguyen1409.usermanagement.dto.request.UpdateUserPasswordRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;

public interface ProfileService {

    UserResponse view();

    UserResponse update(UpdateUserRequest updateUserRequest);

    void updatePassword(UpdateUserPasswordRequest request);

    void delete();
}
