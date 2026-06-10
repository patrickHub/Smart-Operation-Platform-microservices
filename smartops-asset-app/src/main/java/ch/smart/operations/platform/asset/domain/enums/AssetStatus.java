package ch.smart.operations.platform.asset.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AssetStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    UNDER_MAINTENANCE("Under Maintenance"),
    RETIRED("Retired");

    private final String value;

    AssetStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AssetStatus fromValue(String value) {
        for (AssetStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
}
