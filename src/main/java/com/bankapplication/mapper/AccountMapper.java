package com.bankapplication.mapper;

import com.bankapplication.dto.AccountDto;
import com.bankapplication.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "userId", source = "user.id") //  MapStruct pulls user.id into userId
    AccountDto toAccountDto(Account account);
}
