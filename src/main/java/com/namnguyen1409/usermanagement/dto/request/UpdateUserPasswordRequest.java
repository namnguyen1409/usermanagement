package com.namnguyen1409.usermanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserPasswordRequest {
    @NotNull(message = "user.update.oldPassword.notNull")
    String oldPassword;

    @NotNull(message = "user.password.notNull")
    @Size(min = 6, max= 100, message = "user.password.size")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "user.password.pattern"
    )
    String newPassword;
}
