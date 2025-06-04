package com.namnguyen1409.usermanagement.specification;

import com.namnguyen1409.usermanagement.dto.request.FilterUserRequest;
import com.namnguyen1409.usermanagement.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification extends BaseSpecification {

    public static Specification<User> buildSpecification(FilterUserRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            addLikeIfNotEmpty(criteriaBuilder, predicates, root.get(User.Fields.username), filterRequest.getUsername());
            addLikeIfNotEmpty(criteriaBuilder, predicates, root.get(User.Fields.firstName), filterRequest.getFirstName());

            addLikeIfNotEmpty(criteriaBuilder, predicates, root.get(User.Fields.lastName), filterRequest.getLastName());
            addLikeIfNotEmpty(criteriaBuilder, predicates, root.get(User.Fields.email), filterRequest.getEmail());
            addLikeIfNotEmpty(criteriaBuilder, predicates, root.get(User.Fields.phone), filterRequest.getPhone());
            addEqualIfNotNull(criteriaBuilder, predicates, root.get(User.Fields.gender), filterRequest.getGender());
            addRange(criteriaBuilder, predicates,
                    root.get(User.Fields.birthday),
                    filterRequest.getBirthdayFrom(),
                    filterRequest.getBirthdayTo()
            );
            addLikeIfNotEmpty(criteriaBuilder, predicates, root.get(User.Fields.address), filterRequest.getAddress());

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
