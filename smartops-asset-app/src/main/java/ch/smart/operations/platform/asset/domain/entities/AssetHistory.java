package ch.smart.operations.platform.asset.domain.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import ch.smart.operations.platform.asset.domain.enums.AssetHistoryEventType;

public class AssetHistory {
    private final UUID id;
    private final UUID assetId;
    private final AssetHistoryEventType eventType;
    private final OffsetDateTime eventTimestamp;
    private final UUID actorId;
    private final String summary;
    private final String payloadJson;
    private final OffsetDateTime createdAt;

    public AssetHistory(
            UUID id,
            UUID assetId,
            AssetHistoryEventType eventType,
            OffsetDateTime eventTimestamp,
            UUID actorId,
            String summary,
            String payloadJson,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.assetId = assetId;
        this.eventType = eventType;
        this.eventTimestamp = eventTimestamp;
        this.actorId = actorId;
        this.summary = summary;
        this.payloadJson = payloadJson;
        this.createdAt = createdAt;
    }

    public static AssetHistory create(
            UUID assetId,
            AssetHistoryEventType eventType,
            OffsetDateTime eventTimestamp,
            UUID actorId,
            String summary,
            String payloadJson
    ) {
        return new AssetHistory(
                UUID.randomUUID(),
                assetId,
                eventType,
                eventTimestamp,
                actorId,
                summary,
                payloadJson,
                OffsetDateTime.now()
        );
    }

    public UUID getId() {
        return this.id;
    }


    public UUID getAssetId() {
        return this.assetId;
    }


    public AssetHistoryEventType getEventType() {
        return this.eventType;
    }


    public OffsetDateTime getEventTimestamp() {
        return this.eventTimestamp;
    }


    public UUID getActorId() {
        return this.actorId;
    }


    public String getSummary() {
        return this.summary;
    }


    public String getPayloadJson() {
        return this.payloadJson;
    }


    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }


    


}
