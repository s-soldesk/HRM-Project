package com.hrm.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.hrm.dto.UserAccounts;

@Mapper
public interface UserAccountsMapper {
    UserAccounts findByUsername(String username);
}
