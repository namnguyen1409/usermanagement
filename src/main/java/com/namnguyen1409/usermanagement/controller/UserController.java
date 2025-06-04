package com.namnguyen1409.usermanagement.controller;

import com.namnguyen1409.usermanagement.dto.request.CreateUserRequest;
import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.FilterUserRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.CustomApiResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponseDetail;
import com.namnguyen1409.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "User", description = "API for user management")
public class UserController {
    UserService userService;

    @PreAuthorize("hasAnyAuthority('VIEW_USER')")
    @GetMapping
    public CustomApiResponse<Page<UserResponse>> view() {
        var response = userService.filterUsers(new FilterUserRequest());
        return CustomApiResponse.<Page<UserResponse>>builder()
                .data(response)
                .build();
    }

    @PreAuthorize("hasAnyAuthority('VIEW_USER')")
    @PostMapping
    public CustomApiResponse<Page<UserResponse>> filter(@RequestBody FilterUserRequest request) {
        var response = userService.filterUsers(request);
        return CustomApiResponse.<Page<UserResponse>>builder()
                .data(response)
                .build();
    }

    @PreAuthorize("hasAnyAuthority('ADD_USER')")
    @PostMapping("/create")
    public CustomApiResponse<UserResponse> create(@RequestBody @Validated CreateUserRequest request) {
        var response = userService.createUser(request);
        return CustomApiResponse.<UserResponse>builder()
                .data(response)
                .build();
    }


    @PreAuthorize("hasAnyAuthority('VIEW_USER')")
    @GetMapping("/{id}")
    public CustomApiResponse<UserResponseDetail> viewById(@PathVariable String id) {
        var response = userService.getUserById(id);
        return CustomApiResponse.<UserResponseDetail>builder()
                .data(response)
                .build();
    }

    @PreAuthorize("hasAnyAuthority('EDIT_USER')")
    @PutMapping("/{id}")
    public CustomApiResponse<UserResponse> update(@PathVariable String id,
                                                  @RequestBody @Validated UpdateUserRequest request) {
        var response = userService.updateUser(id, request);
        return CustomApiResponse.<UserResponse>builder()
                .data(response)
                .build();
    }


    @PreAuthorize("hasAnyAuthority('DELETE_USER')")
    @DeleteMapping("/{id}")
    public CustomApiResponse<Void> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return CustomApiResponse.<Void>builder()
                .build();
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/restore/{id}")
    public CustomApiResponse<Void> restore(@PathVariable String id) {
        userService.restoreUser(id);
        return CustomApiResponse.<Void>builder()
                .build();
    }

    @PreAuthorize("hasAuthority('EDIT_USER')")
    @PostMapping("/unlock/{id}")
    public CustomApiResponse<Void> unlock(@PathVariable String id) {
        userService.unlockUser(id);
        return CustomApiResponse.<Void>builder()
                .build();
    }


    @PreAuthorize("hasAnyAuthority('VIEW_USER')")
    @GetMapping("/login-history/{id}")
    public CustomApiResponse<Page<LoginLogResponse>> loginHistory(@PathVariable String id) {
        var response = userService.getLoginHistory(id, new FilterLoginLog());
        return CustomApiResponse.<Page<LoginLogResponse>>builder()
                .data(response)
                .build();
    }


    @PreAuthorize("hasAnyAuthority('VIEW_USER')")
    @PostMapping("/login-history/{id}")
    public CustomApiResponse<Page<LoginLogResponse>> loginHistory(@PathVariable @Schema(name = "userId", example = "abed") String id,
                                                                  @RequestBody FilterLoginLog filterRequest) {
        var response = userService.getLoginHistory(id, filterRequest);
        return CustomApiResponse.<Page<LoginLogResponse>>builder()
                .data(response)
                .build();
    }

}
