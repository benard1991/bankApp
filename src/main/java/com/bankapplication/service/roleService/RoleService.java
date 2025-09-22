package com.bankapplication.service.roleService;

import com.bankapplication.model.Role;
import com.bankapplication.model.enums.UserRole;

import java.util.Optional;

public interface RoleService {

    public Role getRoleByName(String roleName);
    Role save(Role role);
    Optional<Role> findByName(String roleName);  // For checking before creating
    public Role getOrCreateRole(UserRole userRoleEnum);
}
