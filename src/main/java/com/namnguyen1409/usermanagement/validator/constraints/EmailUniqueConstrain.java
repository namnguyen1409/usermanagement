package com.namnguyen1409.usermanagement.validator.constraints;

import com.namnguyen1409.usermanagement.validator.EmailUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = EmailUniqueValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailUniqueConstrain {
    String message() default "{user.email.unique}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
