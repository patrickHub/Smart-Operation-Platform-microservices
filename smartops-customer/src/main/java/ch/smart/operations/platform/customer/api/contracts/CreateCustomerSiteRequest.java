package ch.smart.operations.platform.customer.api.contracts;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ch.smart.operations.platform.customer.api.validation.ValidCountryCode;
import ch.smart.operations.platform.customer.api.validation.ValidTimezone;

public record CreateCustomerSiteRequest(
    @NotBlank(message = "siteName is required")
    @Size(min = 2, max = 255, message = "siteName must be between 2 and 255 characters")
    String siteName,

    @NotBlank(message = "addressLine1 is required")
    @Size(min = 2, max = 255, message = "addressLine1 must be between 2 and 255 characters")
    String addressLine1,

    @Size(max = 255, message = "addressLine2 must not exceed 255 characters")
    String addressLine2,

    @NotBlank(message = "city is required")
    @Size(min = 2, max = 100, message = "city must be between 2 and 100 characters")
    String city,

    @NotBlank(message = "stateRegion is required")
    @Size(max = 100, message = "stateRegion must not exceed 100 characters")
    String stateRegion,

    @NotBlank(message = "postalCode is required")
    @Size(min = 2, max = 30, message = "postalCode must be between 2 and 30 characters")
    String postalCode,

    @NotBlank(message = "countryCode is required")
    @ValidCountryCode
    String countryCode,

    @NotBlank(message = "timezone is required")
    @ValidTimezone
    String timezone,

    @Size(max = 2000, message = "accessInstructions must not exceed 2000 characters")
    String accessInstructions
) {
    
}
