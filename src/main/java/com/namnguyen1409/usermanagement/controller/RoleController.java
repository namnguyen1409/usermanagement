package com.namnguyen1409.usermanagement.controller;

import com.namnguyen1409.usermanagement.dto.response.ApiResponse;
import com.namnguyen1409.usermanagement.dto.response.RoleResponse;
import com.namnguyen1409.usermanagement.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {

    RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleResponse>> view() {
        var result = roleService.getAllRoles();
        return ApiResponse.<List<RoleResponse>>builder()
                .data(result)
                .build();
    }
}
