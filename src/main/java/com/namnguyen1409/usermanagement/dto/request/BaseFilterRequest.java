package com.namnguyen1409.usermanagement.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BaseFilterRequest {
    @Min(0)
    @Builder.Default
    Integer page = 0;
    @Min(1)
    @Builder.Default
    Integer size = 10;
    @Builder.Default
    String sortBy = "createdAt";
    @Builder.Default
    String sortDirection = "asc";
}
