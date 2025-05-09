package com.namnguyen1409.usermanagement.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
    String token;
    String refreshToken;
    String loginLogId;
    Boolean isAuthenticated;
}
