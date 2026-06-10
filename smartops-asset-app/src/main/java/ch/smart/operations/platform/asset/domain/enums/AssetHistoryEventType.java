package ch.smart.operations.platform.asset.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AssetHistoryEventType {

    REGISTERED("Registered"),
    STATUS_CHANGED("Status Changed"),
    UPDATED("Updated"),
    MAINTENANCE_PERFORMED("Maintenance Performed");

    private final String value;

    AssetHistoryEventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AssetHistoryEventType  fromValue(String value) {
        for (AssetHistoryEventType eventType : values()) {
            if (eventType.value.equalsIgnoreCase(value)) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
    
}
