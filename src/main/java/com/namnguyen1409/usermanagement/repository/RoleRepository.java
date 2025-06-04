package com.namnguyen1409.usermanagement.repository;

import com.namnguyen1409.usermanagement.constants.enums.UserRole;
import com.namnguyen1409.usermanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, UserRole> {

    Role findByName(UserRole name);

}