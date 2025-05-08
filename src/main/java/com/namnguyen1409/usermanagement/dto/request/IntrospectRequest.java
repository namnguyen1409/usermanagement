package com.namnguyen1409.usermanagement.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * IntrospectRequest is a data transfer object that represents a request to introspect a token.
 * It is used to encapsulate the information needed to perform the introspection operation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectRequest {
    String jti;
}
