package ch.smart.operations.platform.workorder.application.dtos;

import java.util.UUID;

public record AssetSummaryDto(
        UUID id,
        String assetNumber,
        String serialNumber,
        UUID customerId,
        UUID siteId,
        String name,
        String status,
        String criticality
) {
}