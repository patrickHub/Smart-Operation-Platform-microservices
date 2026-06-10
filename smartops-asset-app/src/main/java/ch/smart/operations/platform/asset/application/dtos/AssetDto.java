package ch.smart.operations.platform.asset.application.dtos;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;


public record AssetDto(
    UUID id,
    String assetNumber,
    String serialNumber,
    UUID assetTypeId,
    UUID siteId,
    UUID customerId,
    String name,
    String description,
    LocalDate installationDate,
    LocalDate warrantyEndDate,
    String status,
    String criticality,
    OffsetDateTime createdAt,
    String createdBy,
    OffsetDateTime updatedAt,
    String updatedBy
) {
    
}
