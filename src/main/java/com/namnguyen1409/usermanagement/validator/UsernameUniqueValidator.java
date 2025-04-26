package com.namnguyen1409.usermanagement.validator;

import com.namnguyen1409.usermanagement.repository.UserRepository;
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
    UserRepository userRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !userRepository.existsByUsername(value);
    }
}
