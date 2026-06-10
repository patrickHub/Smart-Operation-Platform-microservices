package ch.smart.operations.platform.asset.application.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AssetTypeDto(
    UUID id,
    String code,
    String name,
    String manufacturer,
    String model,
    String description,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    
}
