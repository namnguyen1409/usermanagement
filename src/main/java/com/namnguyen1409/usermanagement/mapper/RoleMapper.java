package com.namnguyen1409.usermanagement.mapper;

import com.namnguyen1409.usermanagement.constants.enums.UserRole;
import com.namnguyen1409.usermanagement.dto.response.PermissionResponse;
import com.namnguyen1409.usermanagement.dto.response.RoleResponse;
import com.namnguyen1409.usermanagement.entity.Permission;
import com.namnguyen1409.usermanagement.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    default String mapRoleToName(UserRole role) {
        if (role == null) return null;
        return role.name();
    }

    default Set<PermissionResponse> mapPermissionsToPermissionResponses(Set<Permission> permissions) {
        if (permissions == null) return Collections.emptySet();
        return permissions.stream()
                .map(permission -> PermissionResponse.builder()
                        .name(permission.getName().name())
                        .description(permission.getDescription())
                        .build())
                .collect(Collectors.toSet());
    }

    RoleResponse toRoleResponse(Role role);

}
