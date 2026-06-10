package ch.smart.operations.platform.customer.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimezoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimezone {
    String message() default "timezone must be a valid IANA timezone, for example Europe/Zurich";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}