package com.namnguyen1409.usermanagement.specification;

import com.namnguyen1409.usermanagement.dto.request.FilterLoginLog;
import com.namnguyen1409.usermanagement.entity.LoginLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LoginLogSpecification {
    public static Specification<LoginLog> buildSpecification(FilterLoginLog filterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest.getId() != null && !filterRequest.getId().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filterRequest.getId()));
            }

            if (filterRequest.getUserId() != null && !filterRequest.getUserId().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), filterRequest.getUserId()));
            }

            if(filterRequest.getCreatedAtFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filterRequest.getCreatedAtFrom()));
            }
            if(filterRequest.getCreatedAtTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filterRequest.getCreatedAtTo()));
            }
            if (filterRequest.getSuccess() != null) {
                predicates.add(criteriaBuilder.equal(root.get("success"), filterRequest.getSuccess()));
            }
            if (filterRequest.getUserAgent() != null && !filterRequest.getUserAgent().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("userAgent"), "%" + filterRequest.getUserAgent() + "%"));
            }
            if (filterRequest.getIpAddress() != null && !filterRequest.getIpAddress().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("ipAddress"), "%" + filterRequest.getIpAddress() + "%"));
            }
            if (filterRequest.getDevice() != null && !filterRequest.getDevice().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("device"), "%" + filterRequest.getDevice() + "%"));
            }
            if (filterRequest.getBrowser() != null && !filterRequest.getBrowser().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("browser"), "%" + filterRequest.getBrowser() + "%"));
            }
            if (filterRequest.getBrowserVersion() != null && !filterRequest.getBrowserVersion().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("browserVersion"), "%" + filterRequest.getBrowserVersion() + "%"));
            }
            if (filterRequest.getOs() != null && !filterRequest.getOs().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("os"), "%" + filterRequest.getOs() + "%"));
            }
            if (filterRequest.getOsVersion() != null && !filterRequest.getOsVersion().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("osVersion"), "%" + filterRequest.getOsVersion() + "%"));
            }

            if (filterRequest.getLogout() != null) {
                predicates.add(criteriaBuilder.equal(root.get("logout"), filterRequest.getLogout()));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
