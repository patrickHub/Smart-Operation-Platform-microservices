package ch.smart.operations.platform.customer.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CountryCodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCountryCode {
    String message() default "countryCode must be a valid ISO 3166-1 alpha-2 code, for example CH or FR";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}