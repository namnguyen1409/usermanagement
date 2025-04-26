package com.namnguyen1409.usermanagement.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String firstName;
    String lastName;
    String email;
    String phone;
    Boolean gender;
    LocalDate birthday;
    String address;
    Boolean isDeleted;
    Set<String> roles;
    Set<String> revokedPermissions;
}
