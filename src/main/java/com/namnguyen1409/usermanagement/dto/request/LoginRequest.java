package com.namnguyen1409.usermanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * LoginRequest is a data transfer object representing a request to log in to the system.
 * It encapsulates the user's credentials, such as username and password, required
 * for authentication purposes.
 *
 * <p>This class is intended to be used as input for authentication services to facilitate
 * the process of verifying user credentials.</p>
 *
 * <p>Annotations used:
 * <ul>
 *   <li>{@code @Data} - Generates getters, setters, and other utility methods.</li>
 *   <li>{@code @NoArgsConstructor} - Generates a no-argument constructor.</li>
 *   <li>{@code @AllArgsConstructor} - Generates a constructor with arguments for all fields.</li>
 *   <li>{@code @Builder} - Provides a builder pattern for the class.</li>
 *   <li>{@code @FieldDefaults(level = AccessLevel.PRIVATE)} - Sets the default access level
 *       of the fields to private for enhanced encapsulation.</li>
 * </ul>
 * </p>
 *
 * Fields:
 * <ul>
 *   <li>username - The username of the user attempting to log in.</li>
 *   <li>password - The password of the user attempting to log in.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @NotNull(message = "user.username.notNull")
    String username;
    @NotNull(message = "user.password.notNull")
    String password;

    Boolean rememberMe = false;
}
