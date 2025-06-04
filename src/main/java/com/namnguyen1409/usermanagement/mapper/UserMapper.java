package com.namnguyen1409.usermanagement.mapper;

import com.namnguyen1409.usermanagement.dto.request.CreateUserRequest;
import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.dto.response.UserResponse;
import com.namnguyen1409.usermanagement.dto.response.UserResponseDetail;
import com.namnguyen1409.usermanagement.entity.Permission;
import com.namnguyen1409.usermanagement.entity.Role;
import com.namnguyen1409.usermanagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "revokedPermissions", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(CreateUserRequest createUserRequest);

    default Set<String> mapRolesToNames(Set<Role> roles) {
        if (roles == null) return Collections.emptySet();
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    default Set<String> mapPermissionsToNames(Set<Permission> permissions) {
        if (permissions == null) return Collections.emptySet();
        return permissions.stream()
                .map(permission -> permission.getName().name())
                .collect(Collectors.toSet());
    }

    UserResponse toUserResponse(User user);

    UserResponseDetail toUserResponseDetail(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "revokedPermissions", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateUser(@MappingTarget User user, UpdateUserRequest request);
}