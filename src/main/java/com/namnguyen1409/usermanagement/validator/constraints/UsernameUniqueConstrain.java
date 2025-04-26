package com.namnguyen1409.usermanagement.validator.constraints;

import com.namnguyen1409.usermanagement.validator.UsernameUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameUniqueValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameUniqueConstrain {
    String message() default "{user.username.unique}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
