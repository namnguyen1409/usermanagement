package com.namnguyen1409.usermanagement.validator;

import com.namnguyen1409.usermanagement.service.cache.UserCacheService;
import com.namnguyen1409.usermanagement.validator.constraints.UsernameUniqueConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UsernameUniqueValidator implements ConstraintValidator<UsernameUniqueConstrain, String> {
    UserCacheService userCacheService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !userCacheService.isUsernameExists(value);
    }
}
