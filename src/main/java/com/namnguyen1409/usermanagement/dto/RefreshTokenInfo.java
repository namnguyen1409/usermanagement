package com.namnguyen1409.usermanagement.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@ToString
public class RefreshTokenInfo implements Serializable {
    String userId;
    String sessionId;
    LocalDateTime expiresAt;
}
