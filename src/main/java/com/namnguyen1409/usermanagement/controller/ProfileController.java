package com.namnguyen1409.usermanagement.controller;

import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserPasswordRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.CustomApiResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import com.namnguyen1409.usermanagement.service.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Profile", description = "API for user profile management")
public class ProfileController {
    ProfileService profileService;


    @GetMapping
    public CustomApiResponse<UserResponse> view() {
        var response = profileService.view();
        return CustomApiResponse.<UserResponse>builder()
                .data(response)
                .build();
    }


    @PutMapping
    public CustomApiResponse<UserResponse> update(@RequestBody @Validated UpdateUserRequest request) {
        var response = profileService.update(request);
        return CustomApiResponse.<UserResponse>builder()
                .data(response)
                .build();
    }


    @PutMapping("/password")
    public CustomApiResponse<Void> updatePassword(@RequestBody @Validated UpdateUserPasswordRequest request) {
        profileService.updatePassword(request);
        return CustomApiResponse.<Void>builder()
                .build();
    }


    @DeleteMapping
    public CustomApiResponse<Void> delete() {
        profileService.delete();
        return CustomApiResponse.<Void>builder()
                .build();
    }


    @GetMapping("/login-history")
    public CustomApiResponse<Page<LoginLogResponse>> loginHistory() {
        var response = profileService.getLoginHistory(new FilterLoginLog());
        return CustomApiResponse.<Page<LoginLogResponse>>builder()
                .data(response)
                .build();
    }


    @PostMapping("/login-history")
    public CustomApiResponse<Page<LoginLogResponse>> loginHistory(@RequestBody FilterLoginLog filterRequest) {
        var response = profileService.getLoginHistory(filterRequest);
        return CustomApiResponse.<Page<LoginLogResponse>>builder()
                .data(response)
                .build();
    }


    @PostMapping("/revoke/{loginLogId}")
    public CustomApiResponse<Void> revokeLoginLog(@PathVariable String loginLogId) {
        profileService.revokeLoginLog(loginLogId);
        return CustomApiResponse.<Void>builder()
                .build();
    }

}
