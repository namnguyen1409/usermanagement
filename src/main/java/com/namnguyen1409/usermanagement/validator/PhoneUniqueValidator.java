package com.namnguyen1409.usermanagement.validator;

import com.namnguyen1409.usermanagement.repository.UserRepository;
import com.namnguyen1409.usermanagement.validator.constraints.PhoneUniqueConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PhoneUniqueValidator implements ConstraintValidator<PhoneUniqueConstrain, String> {

    UserRepository userRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !userRepository.existsByPhone(value);
    }
}
