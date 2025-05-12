package com.namnguyen1409.usermanagement.controller;

import com.namnguyen1409.usermanagement.dto.request.*;
import com.namnguyen1409.usermanagement.dto.response.*;
import com.namnguyen1409.usermanagement.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    CustomApiResponse<LoginResponse> login(@RequestBody LoginRequest request,
                                           HttpServletRequest httpServletRequest,
                                           HttpServletResponse httpServletResponse
    ) {
        var response = authenticationService.login(request, httpServletRequest, httpServletResponse);
        return CustomApiResponse.<LoginResponse>builder()
                .data(response)
                .build();
    }

    @PostMapping("/logout")
    CustomApiResponse<Void> logout(@RequestHeader("Authorization") String authorization,
                                   HttpServletResponse httpServletResponse) {
        String token = authorization.replace("Bearer ", "");
        authenticationService.logout(LogoutRequest.builder()
                            .token(token).build(), httpServletResponse);
        return CustomApiResponse.<Void>builder()
                .build();
    }


    @PostMapping("/refresh-token")
    CustomApiResponse<RefreshTokenResponse> refreshToken(HttpServletRequest httpServletRequest) {
        var response = authenticationService.refreshToken(httpServletRequest);
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
