package com.namnguyen1409.usermanagement.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterLoginLog extends BaseFilterRequest{
    String id;
    String userId;
    LocalDateTime createdAtFrom;
    LocalDateTime createdAtTo;
    Boolean success;
    String userAgent;
    String ipAddress;
    String device;
    String browser;
    String browserVersion;
    String os;
    String osVersion;
}
