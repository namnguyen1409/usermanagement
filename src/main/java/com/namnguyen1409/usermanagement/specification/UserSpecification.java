package com.namnguyen1409.usermanagement.specification;

import com.namnguyen1409.usermanagement.dto.request.FilterUserRequest;
import com.namnguyen1409.usermanagement.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> buildSpecification(FilterUserRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest.getUsername() != null && !filterRequest.getUsername().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + filterRequest.getUsername() + "%"));
            }

            if (filterRequest.getFirstName() != null && !filterRequest.getFirstName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("firstName"), "%" + filterRequest.getFirstName() + "%"));
            }

            if (filterRequest.getLastName() != null && !filterRequest.getLastName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("lastName"), "%" + filterRequest.getLastName() + "%"));
            }

            if (filterRequest.getEmail() != null && !filterRequest.getEmail().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + filterRequest.getEmail() + "%"));
            }

            if (filterRequest.getPhone() != null && !filterRequest.getPhone().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("phone"), "%" + filterRequest.getPhone() + "%"));
            }

            if (filterRequest.getGender() != null) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), filterRequest.getGender()));
            }

            if (filterRequest.getBirthdayFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), filterRequest.getBirthdayFrom()));
            }

            if (filterRequest.getBirthdayTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), filterRequest.getBirthdayTo()));
            }

            if (filterRequest.getAddress() != null && !filterRequest.getAddress().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("address"), "%" + filterRequest.getAddress() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
