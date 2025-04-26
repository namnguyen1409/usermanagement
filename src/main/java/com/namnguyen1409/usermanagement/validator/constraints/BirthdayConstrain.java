package com.namnguyen1409.usermanagement.validator.constraints;

import com.namnguyen1409.usermanagement.validator.BirthdayValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BirthdayValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BirthdayConstrain {
    String message() default "{user.birthday.valid}";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    int min() default 18;
    int max() default 100;
}
