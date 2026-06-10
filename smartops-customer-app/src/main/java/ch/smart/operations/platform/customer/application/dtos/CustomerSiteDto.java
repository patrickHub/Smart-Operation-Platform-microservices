package ch.smart.operations.platform.customer.application.dtos;

import java.util.UUID;

import ch.smart.operations.platform.customer.domain.enums.CustomerSiteStatus;

public record CustomerSiteDto(
    UUID id,
    String siteNumber,
    UUID customerId,
    String siteName,
    CustomerSiteStatus status,
    String addressLine1,
    String addressLine2,
    String city,
    String stateRegion,
    String postalCode,
    String countryCode,
    String timezone,
    String accessInstructions,
    Long version,
    String createdAt
) {
    
}
