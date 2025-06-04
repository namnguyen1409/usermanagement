package com.namnguyen1409.usermanagement.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDetail {
    String id;
    String username;
    String firstName;
    String lastName;
    String email;
    String phone;
    Boolean gender;
    LocalDate birthday;
    String address;
    Set<String> roles;
    Set<String> revokedPermissions;
    LocalDateTime createdAt;
    String createdBy;
    LocalDateTime updatedAt;
    String updatedBy;
    Boolean isDeleted;
    LocalDateTime deletedAt;
    String deletedBy;
    Boolean isLocked;
    LocalDateTime lockedAt;
}
