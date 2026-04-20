package ch.smart.operations.platform.customer.api.contracts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerContactRequest(

        @NotBlank(message = "firstName is required.")
        @Size(min = 2, max = 100, message = "firstName must be between 2 and 100 characters.")
        String firstName,

        @NotBlank(message = "lastName is required.")
        @Size(min = 2, max = 100, message = "lastName must be between 2 and 100 characters.")
        String lastName,

        @NotBlank(message = "email is required.")
        @Email(message = "email must be a valid email address.")
        @Size(max = 255, message = "email must not exceed 255 characters.")
        String email,

        @Size(max = 50, message = "phone must not exceed 50 characters.")
        String phone,

        @Size(max = 100, message = "contactRole must not exceed 100 characters.")
        String contactRole,

        boolean isPrimary
) {
}