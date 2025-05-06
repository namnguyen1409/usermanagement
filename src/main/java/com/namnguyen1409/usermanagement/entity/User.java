package com.namnguyen1409.usermanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "USERS")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    String username;

    @Column(nullable = false, length = 100)
    String password;

    @Column(nullable = false, length = 50)
    String firstName;

    @Column(nullable = false, length = 50)
    String lastName;

    @Column(nullable = false, unique = true, length = 100)
    String email;

    @Column(nullable = false, unique = true, length = 20)
    String phone;

    @Column(nullable = false)
    Boolean gender;

    @Column(nullable = false)
    LocalDate birthday;

    @Column(nullable = false)
    String address;

    @Column
    Boolean isLocked = false;

    @Column
    LocalDateTime LockedAt;

    @ManyToMany
    Set<Role> roles;

    @ManyToMany
    Set<Permission> revokedPermissions;
}