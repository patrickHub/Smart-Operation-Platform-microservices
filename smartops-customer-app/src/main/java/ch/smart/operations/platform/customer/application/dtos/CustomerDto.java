package ch.smart.operations.platform.customer.application.dtos;

import java.util.UUID;

import ch.smart.operations.platform.customer.domain.enums.CustomerStatus;

public record CustomerDto(
    UUID id,
    String customerNumber,
    String legalName,
    String displayName,
    CustomerStatus status,
    String taxIdentifier,
    String industry,
    String notes
) {}
