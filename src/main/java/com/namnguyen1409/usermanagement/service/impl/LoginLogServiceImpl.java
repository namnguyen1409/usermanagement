package com.namnguyen1409.usermanagement.service.impl;

import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.dto.response.LoginLogResponse;
import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.mapper.LoginLogMapper;
import com.namnguyen1409.usermanagement.repository.LoginLogRepository;
import com.namnguyen1409.usermanagement.service.LoginLogService;
import com.namnguyen1409.usermanagement.specification.LoginLogSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {


    LoginLogRepository loginLogRepository;
    LoginLogMapper loginLogMapper;

    @NotNull
    @Override
    public Page<LoginLogResponse> getLoginLogResponses(FilterLoginLog filterLoginLog, User user) {
        filterLoginLog.setUserId(user.getId());
        Sort sortDirection = "asc".equalsIgnoreCase(filterLoginLog.getSortDirection())
                ? Sort.by(filterLoginLog.getSortBy()).ascending()
                : Sort.by(filterLoginLog.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filterLoginLog.getPage(), filterLoginLog.getSize(), sortDirection);
        var spec = LoginLogSpecification.buildSpecification(filterLoginLog);
        return loginLogRepository.findAll(spec, pageable).map(loginLogMapper::toLoginLogResponse);
    }
}
