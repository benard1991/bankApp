package com.bankapplication.mapper;

import com.bankapplication.dto.AccountDto;
import com.bankapplication.dto.RoleDto;
import com.bankapplication.dto.UserProfileDto;
import com.bankapplication.model.Account;
import com.bankapplication.model.Role;
import com.bankapplication.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    AccountDto toAccountDto(Account account);
    RoleDto toRoleDto(Role role);
    UserProfileDto toUserProfileDto(User user);
}
