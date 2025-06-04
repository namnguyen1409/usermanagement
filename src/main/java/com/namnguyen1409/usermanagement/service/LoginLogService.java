package com.namnguyen1409.usermanagement.service;

import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;

public interface LoginLogService {
    @NotNull Page<LoginLogResponse> getLoginLogResponses(FilterLoginLog filterLoginLog, User user);
}
