package com.namnguyen1409.usermanagement.controller;

import com.namnguyen1409.usermanagement.dto.request.CreateUserRequest;
import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.request.FilterUserRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.ApiResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponseDetail;
import com.namnguyen1409.usermanagement.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @GetMapping
    public ApiResponse<Page<UserResponse>> view() {
        var response = userService.filterUsers(new FilterUserRequest());
        return ApiResponse.<Page<UserResponse>>builder()
                .data(response)
                .build();
    }

    @PostMapping
    public ApiResponse<Page<UserResponse>> filter(@RequestBody FilterUserRequest request) {
        var response = userService.filterUsers(request);
        return ApiResponse.<Page<UserResponse>>builder()
                .data(response)
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<UserResponse> create(@RequestBody @Validated CreateUserRequest request) {
        var response = userService.createUser(request);
        return ApiResponse.<UserResponse>builder()
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponseDetail> viewById(@PathVariable String id) {
        var response = userService.getUserById(id);
        return ApiResponse.<UserResponseDetail>builder()
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(@PathVariable String id, @RequestBody @Validated UpdateUserRequest request) {
        var response = userService.updateUser(id, request);
        return ApiResponse.<UserResponse>builder()
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/restore/{id}")
    public ApiResponse<Void> restore(@PathVariable String id) {
        userService.restoreUser(id);
        return ApiResponse.<Void>builder()
                .build();
    }

    /**
     * Lấy lịch sử đăng nhập của người dùng dựa trên ID.
     *
     * @param id ID của người dùng cần lấy lịch sử đăng nhập.
     * @return Đối tượng {@link ApiResponse} chứa dữ liệu là một trang các đối tượng {@link LoginLogResponse}.
     */
    @GetMapping("/login-history/{id}")
    public ApiResponse<Page<LoginLogResponse>> loginHistory(@PathVariable String id) {
        var response = userService.getLoginHistory(id, new FilterLoginLog());
        return ApiResponse.<Page<LoginLogResponse>>builder()
                .data(response)
                .build();
    }

    /**
     * Lấy lịch sử đăng nhập của người dùng theo ID và bộ lọc được yêu cầu.
     *
     * @param id ID của người dùng cần lấy lịch sử đăng nhập.
     * @param filterRequest Đối tượng {@link FilterLoginLog} chứa các tiêu chí lọc lịch sử đăng nhập.
     * @return Đối tượng {@link ApiResponse} chứa dữ liệu là một trang các đối tượng {@link LoginLogResponse}.
     */
    @PostMapping("/login-history/{id}")
    public ApiResponse<Page<LoginLogResponse>> loginHistory(@PathVariable String id, @RequestBody FilterLoginLog filterRequest) {
        var response = userService.getLoginHistory(id, filterRequest);
        return ApiResponse.<Page<LoginLogResponse>>builder()
                .data(response)
                .build();
    }

}
