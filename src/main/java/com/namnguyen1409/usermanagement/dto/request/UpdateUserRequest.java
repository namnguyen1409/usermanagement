package com.namnguyen1409.usermanagement.dto.request;

import com.namnguyen1409.usermanagement.validator.constraints.BirthdayConstrain;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class UpdateUserRequest {
    @Size(min = 6, max = 20, message = "{user.username.size}")
    @Pattern(regexp = "^\\w+$", message = "{user.username.pattern}")
    String username;

    @Size(min = 2, max = 20, message = "{user.firstName.size}")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "{user.firstName.pattern}")
    String firstName;

    @Size(min = 2, max = 20, message = "{user.lastName.size}")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "{user.lastName.pattern}")
    String lastName;

    @Email(message = "{user.email.valid}")
    String email;

    @Pattern(regexp = "^\\d{10}$", message = "{user.phone.pattern}")
    String phone;

    Boolean gender;

    @BirthdayConstrain
    LocalDate birthday;

    @Size(min = 10, max = 255, message = "{user.address.size}")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s,.\\-]+$", message = "{user.address.pattern}")
    String address;

    Set<String> roles;
    Set<String> revokedPermissions;
}
