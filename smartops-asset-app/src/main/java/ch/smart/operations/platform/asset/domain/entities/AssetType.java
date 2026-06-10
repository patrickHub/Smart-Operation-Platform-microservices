package ch.smart.operations.platform.asset.domain.entities;

import java.time.OffsetDateTime;
import java.util.UUID;


public class AssetType {


    private UUID id;
    private String code;
    private String name;
    private String manufacturer;
    private String model;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;


    public AssetType(UUID id, String code, String name, String manufacturer, String model, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AssetType create(String code, String name, String manufacturer, String model, String description) {
        return new AssetType(
            UUID.randomUUID(),
            code,
            name,
            manufacturer,
            model,
            description,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
    }


    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
}
