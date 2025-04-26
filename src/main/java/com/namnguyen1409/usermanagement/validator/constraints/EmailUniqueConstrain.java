package com.namnguyen1409.usermanagement.validator.constraints;

import com.namnguyen1409.usermanagement.validator.EmailUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * <p>
 * The {@code EmailUniqueConstrain} annotation is used to enforce the uniqueness
 * of email addresses during validation processes.
 * </p>
 * <p>
 * This constraint annotation ensures that a given email value does not already
 * exist within the underlying data repository. It relies on the {@code EmailUniqueValidator}
 * class for validation logic, which queries the {@code UserRepository} to check
 * if the email is already present.
 * </p>
 * <p>
 * This annotation is typically applied to fields within a class that requires email
 * uniqueness validation.
 * </p>
 * <p>
 * Properties:
 * <ul>
 *   <li>{@code message}: Specifies the error message if the validation fails. By default, the message is "user.email.unique".</li>
 *   <li>{@code groups}: Allows specification of validation groups for categorizing constraints.</li>
 *   <li>{@code payload}: Can be used to attach additional metadata information for the validation failure.</li>
 * </ul>
 * </p>
 * <p>
 * Applicable to fields only, as defined by the {@code ElementType.FIELD} target.
 * </p>
 * <p>
 * Annotations Used:
 * <ul>
 *   <li>{@code @Documented}: Indicates that this annotation is included in the Javadoc.</li>
 *   <li>{@code @Constraint}: Marks this as a validation constraint annotation. It links the validation logic to the {@code EmailUniqueValidator} class.</li>
 *   <li>{@code @Target}: Specifies that this annotation can only be applied to fields.</li>
 *   <li>{@code @Retention}: Marks the annotation for retention at runtime, allowing it to be inspected via reflection.</li>
 * </ul>
 * </p>
 */
@Documented
@Constraint(validatedBy = EmailUniqueValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailUniqueConstrain {
    String message() default "{user.email.unique}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
