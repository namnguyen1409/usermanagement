package com.namnguyen1409.usermanagement.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterLoginLog extends BaseFilterRequest{
    String id;
    String userId;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAtFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAtTo;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime ExpiredAtFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime ExpiredAtTo;
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
