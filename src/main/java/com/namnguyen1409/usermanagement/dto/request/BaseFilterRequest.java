package com.namnguyen1409.usermanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@FieldNameConstants
@Schema(description = "Base filter request")
public class BaseFilterRequest {

    @Schema(description = "Page number", example = "0")
    @Min(0)
    @Builder.Default
    Integer page = 0;

    @Schema(description = "Page size", example = "10")
    @Min(1)
    @Builder.Default
    Integer size = 10;

    @Schema(description = "Sort by", example = "createdAt")
    @Builder.Default
    String sortBy = "createdAt";

    @Schema(description = "Sort direction", example = "desc")
    @Builder.Default
    String sortDirection = "desc";
}
