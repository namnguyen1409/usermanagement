package com.namnguyen1409.usermanagement.entity;

import com.namnguyen1409.usermanagement.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "ROLES")
public class Role {
    @Enumerated(EnumType.STRING)
    @Id
    UserRole name;
    String description;

    @ManyToMany
    @Builder.Default
    Set<Permission> permissions = new HashSet<>();
}
