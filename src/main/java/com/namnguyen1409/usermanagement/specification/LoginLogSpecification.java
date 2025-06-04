package com.namnguyen1409.usermanagement.specification;

import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.entity.BaseEntity;
import com.namnguyen1409.usermanagement.entity.LoginLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LoginLogSpecification extends BaseSpecification {


    public static Specification<LoginLog> buildSpecification(FilterLoginLog filterRequest) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addEqualIfNotEmpty(cb, predicates, root.get(LoginLog.Fields.id), filterRequest.getId());
            addEqualIfNotEmpty(cb, predicates,
                    root.get(LoginLog.Fields.user).get(BaseEntity.Fields.id),
                    filterRequest.getUserId()
            );

            addRange(cb, predicates, root.get(LoginLog.Fields.createdAt),
                    filterRequest.getCreatedAtFrom(),
                    filterRequest.getCreatedAtTo()
            );
            addRange(cb, predicates, root.get(LoginLog.Fields.expiredAt),
                    filterRequest.getExpiredAtFrom(),
                    filterRequest.getExpiredAtTo()
            );

            addEqualIfNotNull(cb, predicates, root.get(LoginLog.Fields.success), filterRequest.getSuccess());
            addEqualIfNotNull(cb, predicates, root.get(LoginLog.Fields.logout), filterRequest.getLogout());

            addLikeIfNotEmpty(cb, predicates, root.get(LoginLog.Fields.userAgent), filterRequest.getUserAgent());
            addLikeIfNotEmpty(cb, predicates, root.get(LoginLog.Fields.ipAddress), filterRequest.getIpAddress());
            addLikeIfNotEmpty(cb, predicates, root.get(LoginLog.Fields.device), filterRequest.getDevice());
            addLikeIfNotEmpty(cb, predicates, root.get(LoginLog.Fields.browser), filterRequest.getBrowser());
            addLikeIfNotEmpty(cb, predicates,
                    root.get(LoginLog.Fields.browserVersion),
                    filterRequest.getBrowserVersion()
            );
            addLikeIfNotEmpty(cb, predicates, root.get(LoginLog.Fields.os), filterRequest.getOs());
            addLikeIfNotEmpty(cb, predicates, root.get(LoginLog.Fields.osVersion), filterRequest.getOsVersion());

            return cb.and(predicates.toArray(new Predicate[0]));

        };
    }
}
