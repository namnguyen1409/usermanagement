package com.namnguyen1409.usermanagement.service;

import com.namnguyen1409.usermanagement.dto.response.RoleResponse;

import java.util.List;


public interface RoleService {

    List<RoleResponse> getAllRoles();
}
