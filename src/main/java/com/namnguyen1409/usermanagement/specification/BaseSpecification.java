package com.namnguyen1409.usermanagement.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.MODULE)
public abstract class BaseSpecification {

    public static void addEqualIfNotEmpty(CriteriaBuilder cb,
                                          List<Predicate> predicates,
                                          Path<String> path,
                                          String value
    ) {
        if (value != null && !value.trim().isEmpty()) {
            predicates.add(cb.equal(path, value));
        }
    }

    public static <T> void addEqualIfNotNull(CriteriaBuilder cb,
                                             List<Predicate> predicates,
                                             Path<T> path,
                                             T value)
    {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }

    public static void addLikeIfNotEmpty(CriteriaBuilder cb,
                                         List<Predicate> predicates,
                                         Path<String> path,
                                         String value
    ) {
        if (value != null && !value.trim().isEmpty()) {
            predicates.add(cb.like(path, "%" + value + "%"));
        }
    }

    public static <T extends Comparable<? super T>> void addRange(
            CriteriaBuilder cb,
            List<Predicate> predicates,
            Path<T> path,
            T from,
            T to
    ) {
        if (from != null) {
            predicates.add(cb.greaterThanOrEqualTo(path, from));
        }
        if (to != null) {
            predicates.add(cb.lessThanOrEqualTo(path, to));
        }
    }

}
