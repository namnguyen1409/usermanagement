package com.namnguyen1409.usermanagement.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginLogResponse {
    String id;
    LocalDateTime createdAt;
    LocalDateTime expiredAt;
    Boolean success;
    String userAgent;
    String ipAddress;
    String device;
    String browser;
    String browserVersion;
    String os;
    String osVersion;
    Boolean logout;
}
