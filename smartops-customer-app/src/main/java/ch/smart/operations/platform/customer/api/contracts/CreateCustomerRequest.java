package ch.smart.operations.platform.customer.api.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public record CreateCustomerRequest(
        @NotBlank(message = "legalName is required")
        @Size(min = 2, max = 255, message = "legalName must be between 2 and 255 characters")
        String legalName,

        @NotBlank(message = "displayName is required")
        @Size(min = 2, max = 255, message = "displayName must be between 2 and 255 characters")
        String displayName,

        @NotBlank(message = "taxIdentifier is required")
        @Size(min = 2, max = 100, message = "taxIdentifier must be between 2 and 100 characters")
        String taxIdentifier,

        @NotBlank(message = "industry is required")
        @Size(min = 2, max = 100, message = "industry must be between 2 and 100 characters")
        String industry,

        @NotBlank(message = "notes is required")
        @Size(max = 2000, message = "notes must not exceed 2000 characters")
        String notes
) {
}