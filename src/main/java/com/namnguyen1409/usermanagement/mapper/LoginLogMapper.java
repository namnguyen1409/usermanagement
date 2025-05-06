package com.namnguyen1409.usermanagement.mapper;

import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.entity.LoginLog;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoginLogMapper {

    LoginLogResponse toLoginLogResponse(LoginLog loginLog);
}
