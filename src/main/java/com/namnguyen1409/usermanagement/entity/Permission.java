package com.namnguyen1409.usermanagement.entity;

import com.namnguyen1409.usermanagement.enums.UserPermission;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "PERMISSIONS")
public class Permission {
    @Enumerated(EnumType.STRING)
    @Id
    UserPermission name;
    String description;
}
