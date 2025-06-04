package com.namnguyen1409.usermanagement.service.impl;

import com.namnguyen1409.usermanagement.dto.response.RoleResponse;
import com.namnguyen1409.usermanagement.mapper.RoleMapper;
import com.namnguyen1409.usermanagement.repository.RoleRepository;
import com.namnguyen1409.usermanagement.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @Override
    @Cacheable(value = "roles", key = "'all'")
    public List<RoleResponse> getAllRoles() {
        log.info("Fetching all roles");
        return roleRepository.findAll().stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

}
