package com.namnguyen1409.usermanagement.controller;

import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserPasswordRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.ApiResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import com.namnguyen1409.usermanagement.service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileController {
    ProfileService profileService;

    @GetMapping
    public ApiResponse<UserResponse> view() {
        var response = profileService.view();
        return ApiResponse.<UserResponse>builder()
                .data(response)
                .build();
    }

    @PutMapping
    public ApiResponse<UserResponse> update(@RequestBody @Validated UpdateUserRequest request) {
        var response = profileService.update(request);
        return ApiResponse.<UserResponse>builder()
                .data(response)
                .build();
    }

    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(@RequestBody UpdateUserPasswordRequest request) {
        profileService.updatePassword(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @DeleteMapping
    public ApiResponse<Void> delete() {
        profileService.delete();
        return ApiResponse.<Void>builder()
                .build();
    }

    @GetMapping("/login-history")
    public ApiResponse<Page<LoginLogResponse>> loginHistory() {
        var response = profileService.getLoginHistory(new FilterLoginLog());
        return ApiResponse.<Page<LoginLogResponse>>builder()
                .data(response)
                .build();
    }

    @PostMapping("/login-history")
    public ApiResponse<Page<LoginLogResponse>> loginHistory(@RequestBody FilterLoginLog filterRequest) {
        var response = profileService.getLoginHistory(filterRequest);
        return ApiResponse.<Page<LoginLogResponse>>builder()
                .data(response)
                .build();
    }

}
