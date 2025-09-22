package com.bankapplication.service.roleService;


import com.bankapplication.model.Role;
import com.bankapplication.model.enums.UserRole;
import com.bankapplication.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Optional<Role> findByName(String roleName) {
        return roleRepository.findByName(roleName);
    }
    @Override
    public Role getOrCreateRole(UserRole userRoleEnum) {
        // Check if the role exists in the DB; if not, create it
        Optional<Role> existingRole = roleRepository.findByName(userRoleEnum.name());
        Role role;
        if (existingRole.isPresent()) {
            role = existingRole.get();
        } else {
            role = new Role(userRoleEnum);
            role.setUserType("DEFAULT");
            role.setCanRegisterAccounts(true);
            role = roleRepository.save(role);
        }
        // Ensure that the users field is initialized before adding the user
        if (role.getUsers() == null) {
            role.setUsers(new HashSet<>());  // Initialize users as an empty set
        }

        return role;
    }

}
