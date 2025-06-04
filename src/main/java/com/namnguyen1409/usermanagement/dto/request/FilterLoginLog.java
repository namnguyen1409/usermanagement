package com.namnguyen1409.usermanagement.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class FilterLoginLog extends BaseFilterRequest {
    String id;
    String userId;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAtFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAtTo;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime expiredAtFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime expiredAtTo;
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
