package com.namnguyen1409.usermanagement.constants.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum UserPermission {
    EDIT_USER("Can edit user", UserRole.ADMIN),
    DELETE_USER("Can delete user", UserRole.ADMIN),
    VIEW_USER("Can view user", UserRole.ADMIN),
    ADD_USER("Can add new user", UserRole.ADMIN),
    ;

    String description;
    UserRole userRole;

}
