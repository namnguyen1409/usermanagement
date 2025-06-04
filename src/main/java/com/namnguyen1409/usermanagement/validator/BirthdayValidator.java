package com.namnguyen1409.usermanagement.validator;


import com.namnguyen1409.usermanagement.validator.constraints.BirthdayConstrain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BirthdayValidator implements ConstraintValidator<BirthdayConstrain, LocalDate> {

    int min;
    int max;

    @Override
    public void initialize(BirthdayConstrain constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        var currentDate = LocalDate.now();
        if (this.min < 0 && this.max < 0) {
            return true;
        }
        if (this.min < 0 && this.max > 0) {
            // handle for check max only
            return value.isBefore(currentDate.plusYears(this.max)) || value.isEqual(currentDate.plusYears(this.max));
        }
        if (this.min > 0 && this.max < 0) {
            // handle for check min only
            return value.isAfter(currentDate.minusYears(this.min)) || value.isEqual(currentDate.minusYears(this.min));
        }
        // handle for check all
        LocalDate minDate = currentDate.minusYears(min);
        LocalDate maxDate = currentDate.minusYears(max);
        return (value.isAfter(maxDate) || value.isEqual(maxDate))
                && (value.isBefore(minDate) || value.isEqual(minDate));
    }
}
