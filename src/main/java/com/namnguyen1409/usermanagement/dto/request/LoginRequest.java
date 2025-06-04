package com.namnguyen1409.usermanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
@Schema(description = "Login request")
public class LoginRequest {

    @Schema(description = "The username of the user attempting to log in", example = "admin")
    @NotNull(message = "user.username.notNull")
    String username;

    @Schema(description = "The password of the user attempting to log in", example = "<PASSWORD>")
    @NotNull(message = "user.password.notNull")
    String password;

    @Schema(description = "Whether the user should be remembered or not", example = "false")
    Boolean rememberMe = false;
}
