package com.namnguyen1409.usermanagement.dto.request;


import com.namnguyen1409.usermanagement.validator.constraints.BirthdayConstrain;
import com.namnguyen1409.usermanagement.validator.constraints.EmailUniqueConstrain;
import com.namnguyen1409.usermanagement.validator.constraints.PhoneUniqueConstrain;
import com.namnguyen1409.usermanagement.validator.constraints.UsernameUniqueConstrain;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    @NotNull(message = "{user.username.notNull}")
    @Size(min = 6, max = 20, message = "{user.username.size}")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "{user.username.pattern}")
    @UsernameUniqueConstrain
    private String username;

    @NotNull(message = "{user.password.notNull}")
    @Size(min = 6, max= 100, message = "{user.password.size}")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "{user.password.pattern}"
    )
    private String password;

    @NotNull(message = "{user.firstName.notNull}")
    @Size(min = 2, max = 20, message = "{user.firstName.size}")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "{user.firstName.pattern}")
    private String firstName;

    @NotNull(message = "{user.lastName.notNull}")
    @Size(min = 2, max = 20, message = "{user.lastName.size}")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "{user.lastName.pattern}")
    private String lastName;

    @NotNull(message = "{user.email.notNull}")
    @Email(message = "{user.email.valid}")
    @EmailUniqueConstrain
    private String email;

    @NotNull(message = "{user.phone.notNull}")
    @Pattern(regexp = "^[0-9]{10}$", message = "{user.phone.pattern}")
    @PhoneUniqueConstrain
    private String phone;

    @NotNull(message = "{user.gender.notNull}")
    private Boolean gender;

    @NotNull(message = "{user.birthday.notNull}")
    @BirthdayConstrain(min=16, max=120, message = "{user.birthday.valid}")
    private LocalDate birthday;

    @NotNull(message = "{user.address.notNull}")
    @Size(min = 10, max = 255, message = "{user.address.size}")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s,.\\-]+$", message = "{user.address.pattern}")
    private String address;

    private Set<String> roleList;
    private Set<String> revokedPermissionList;
}
