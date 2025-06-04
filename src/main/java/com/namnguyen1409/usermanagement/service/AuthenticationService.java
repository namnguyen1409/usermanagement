package com.namnguyen1409.usermanagement.service;

import com.namnguyen1409.usermanagement.dto.request.CreateUserRequest;
import com.namnguyen1409.usermanagement.dto.request.LoginRequest;
import com.namnguyen1409.usermanagement.dto.request.LogoutRequest;
import com.namnguyen1409.usermanagement.dto.response.CreateUserResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginResponse;
import com.namnguyen1409.usermanagement.dto.response.RefreshTokenResponse;


public interface AuthenticationService {

    CreateUserResponse register(CreateUserRequest request);


    LoginResponse login(LoginRequest request);


    void logout(LogoutRequest token);


    RefreshTokenResponse refreshToken();

}
