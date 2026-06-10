package ch.smart.operations.platform.asset.domain.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import ch.smart.operations.platform.asset.domain.enums.AssetCriticality;
import ch.smart.operations.platform.asset.domain.enums.AssetStatus;

public class Asset {
    private final UUID id;
    private final String assetNumber;
    private final String serialNumber;
    private final UUID assetTypeId;
    private final UUID siteId;
    private final UUID customerId;
    private final String name;
    private final String description;
    private final LocalDate installationDate;
    private final LocalDate warrantyEndDate;
    private AssetStatus status;
    private AssetCriticality criticality;
    private Long version;
    private final OffsetDateTime createdAt;
    private final String createdBy;
    private final OffsetDateTime updatedAt;
    private final String updatedBy;

    public Asset(
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
            AssetStatus status,
            AssetCriticality criticality,
            Long version,
            OffsetDateTime createdAt,
            String createdBy,
            OffsetDateTime updatedAt,
            String updatedBy
    ) {
        this.id = id;
        this.assetNumber = assetNumber;
        this.serialNumber = serialNumber;
        this.assetTypeId = assetTypeId;
        this.siteId = siteId;
        this.customerId = customerId;
        this.name = name;
        this.description = description;
        this.installationDate = installationDate;
        this.warrantyEndDate = warrantyEndDate;
        this.status = status;
        this.criticality = criticality;
        this.version = version;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public static Asset create(
            String assetNumber,
            String serialNumber,
            UUID assetTypeId,
            UUID siteId,
            UUID customerId,
            String name,
            String description,
            LocalDate installationDate,
            LocalDate warrantyEndDate,
            AssetCriticality criticality,
            String createdBy,
            String updatedBy
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        return new Asset(
                UUID.randomUUID(),
                assetNumber,
                serialNumber,
                assetTypeId,
                siteId,
                customerId,
                name,
                description,
                installationDate,
                warrantyEndDate,
                AssetStatus.ACTIVE,
                criticality,
                null,
                now,
                createdBy,
                now,
                updatedBy
        );
    }


    public UUID getId() {
        return this.id;
    }


    public String getAssetNumber() {
        return this.assetNumber;
    }


    public String getSerialNumber() {
        return this.serialNumber;
    }


    public UUID getAssetTypeId() {
        return this.assetTypeId;
    }


    public UUID getSiteId() {
        return this.siteId;
    }


    public UUID getCustomerId() {
        return this.customerId;
    }


    public String getName() {
        return this.name;
    }


    public String getDescription() {
        return this.description;
    }


    public LocalDate getInstallationDate() {
        return this.installationDate;
    }


    public LocalDate getWarrantyEndDate() {
        return this.warrantyEndDate;
    }


    public AssetStatus getStatus() {
        return this.status;
    }


    public AssetCriticality getCriticality() {
        return this.criticality;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }


    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }


    public OffsetDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }
    public void setCriticality(AssetCriticality criticality) {
        this.criticality = criticality;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }


}
