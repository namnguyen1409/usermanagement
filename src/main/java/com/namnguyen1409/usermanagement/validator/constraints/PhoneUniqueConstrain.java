package com.namnguyen1409.usermanagement.validator.constraints;

import com.namnguyen1409.usermanagement.validator.PhoneUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneUniqueValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneUniqueConstrain {
    String message() default "{user.phone.unique}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
