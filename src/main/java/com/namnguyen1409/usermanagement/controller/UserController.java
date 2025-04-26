package com.namnguyen1409.usermanagement.controller;

import com.namnguyen1409.usermanagement.dto.request.CreateUserRequest;
import com.namnguyen1409.usermanagement.dto.request.FilterUserRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.ApiResponse;
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




}
