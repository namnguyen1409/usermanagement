package com.namnguyen1409.usermanagement.service;

import com.namnguyen1409.usermanagement.dto.request.*;
import com.namnguyen1409.usermanagement.dto.response.CreateUserResponse;
import com.namnguyen1409.usermanagement.dto.response.IntrospectResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginResponse;
import com.namnguyen1409.usermanagement.dto.response.RefreshTokenResponse;
import com.namnguyen1409.usermanagement.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Giao diện {@code AuthenticationService} cung cấp các phương thức để quản lý
 * và thực hiện các thao tác liên quan đến xác thực người dùng.
 * Các chức năng chính bao gồm kiểm tra tính hợp lệ của token, đăng ký, đăng nhập,
 * đăng xuất và làm mới token xác thực.
 */
public interface AuthenticationService {



    CreateUserResponse register(CreateUserRequest request);


    LoginResponse login(LoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);


    void logout(LogoutRequest token, HttpServletResponse httpServletResponse);


    RefreshTokenResponse refreshToken(HttpServletRequest httpServletRequest);

    void introspect(IntrospectRequest introspectRequest);
}
