package com.namnguyen1409.usermanagement.validator;

import com.namnguyen1409.usermanagement.service.cache.UserCacheService;
import com.namnguyen1409.usermanagement.validator.constraints.EmailUniqueConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailUniqueValidator implements ConstraintValidator<EmailUniqueConstrain, String> {

    UserCacheService userCacheService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !userCacheService.isEmailExists(value);
    }
}
