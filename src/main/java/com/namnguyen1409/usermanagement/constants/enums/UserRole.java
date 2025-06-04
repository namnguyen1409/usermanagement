package com.namnguyen1409.usermanagement.constants.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum UserRole {
    SUPER_ADMIN("The super administrator of the system"),
    ADMIN("The administrator of the system"),
    USER("Normal user of the system");

    String description;
}
