package ch.smart.operations.platform.customer.application.commands;

import java.util.UUID;

public record CreateCustomerSiteCommand(
    UUID customerId,
    String siteName,
    String addressLine1,
    String addressLine2,
    String city,
    String stateRegion,
    String postalCode,
    String countryCode,
    String timezone,
    String accessInstructions
) {
    
}
