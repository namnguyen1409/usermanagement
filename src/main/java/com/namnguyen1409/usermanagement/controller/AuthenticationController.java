package com.namnguyen1409.usermanagement.controller;

import com.namnguyen1409.usermanagement.dto.request.CreateUserRequest;
import com.namnguyen1409.usermanagement.dto.request.LoginRequest;
import com.namnguyen1409.usermanagement.dto.request.LogoutRequest;
import com.namnguyen1409.usermanagement.dto.response.CreateUserResponse;
import com.namnguyen1409.usermanagement.dto.response.CustomApiResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginResponse;
import com.namnguyen1409.usermanagement.dto.response.RefreshTokenResponse;
import com.namnguyen1409.usermanagement.service.AuthenticationService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication", description = "API for user authentication management")
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    CustomApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        var response = authenticationService.login(request);
        return CustomApiResponse.<LoginResponse>builder()
                .data(response)
                .build();
    }


    @PostMapping("/logout")
    CustomApiResponse<Void> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        authenticationService.logout(LogoutRequest.builder()
                .token(token).build());
        return CustomApiResponse.<Void>builder()
                .build();
    }


    @PostMapping("/refresh-token")
    CustomApiResponse<RefreshTokenResponse> refreshToken() {
        var response = authenticationService.refreshToken();
        return CustomApiResponse.<RefreshTokenResponse>builder()
                .data(response)
                .build();
    }


    @PostMapping("/register")
    CustomApiResponse<CreateUserResponse> register(@RequestBody @Validated CreateUserRequest request) {
        var response = authenticationService.register(request);
        return CustomApiResponse.<CreateUserResponse>builder()
                .data(response)
                .build();
    }

}
