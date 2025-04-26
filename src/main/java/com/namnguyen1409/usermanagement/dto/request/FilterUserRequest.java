package com.namnguyen1409.usermanagement.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterUserRequest extends BaseFilterRequest {
    String username;
    String firstName;
    String lastName;
    String email;
    String phone;
    Boolean gender;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate birthdayFrom;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate birthdayTo;
    String address;
}
