package com.namnguyen1409.usermanagement.controller;

import com.namnguyen1409.usermanagement.dto.request.*;
import com.namnguyen1409.usermanagement.dto.response.*;
import com.namnguyen1409.usermanagement.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody LoginRequest request,
                                     HttpServletRequest httpServletRequest) {
        var response = authenticationService.login(request, httpServletRequest);
        return ApiResponse.<LoginResponse>builder()
                .data(response)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest token) {
        authenticationService.logout(token);
        return ApiResponse.<Void>builder()
                .build();
    }


    @PostMapping("/refresh-token")
    ApiResponse<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest token) {
        var response = authenticationService.refreshToken(token);
        return ApiResponse.<RefreshTokenResponse>builder()
                .data(response)
                .build();
    }


    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest token) {
        var response = authenticationService.introspect(token);
        return ApiResponse.<IntrospectResponse>builder()
                .data(response)
                .build();
    }

    @PostMapping("/register")
    ApiResponse<CreateUserResponse> register(@RequestBody @Validated CreateUserRequest request) {
        var response = authenticationService.register(request);
        return ApiResponse.<CreateUserResponse>builder()
                .data(response)
                .build();
    }

}
