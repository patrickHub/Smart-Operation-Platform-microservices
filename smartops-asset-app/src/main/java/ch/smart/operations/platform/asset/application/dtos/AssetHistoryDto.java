package ch.smart.operations.platform.asset.application.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AssetHistoryDto(
    UUID id,
    UUID assetId,
    String eventType,
    OffsetDateTime eventTimestamp,
    UUID actorId,
    String summary,
    String payloadJson,
    OffsetDateTime createdAt
) {
    
}
