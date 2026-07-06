package ch.smart.operations.platform.asset.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AssetCriticality {

    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String value;

    AssetCriticality(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AssetCriticality fromValue(String value) {
        for (AssetCriticality criticality : values()) {
            if (criticality.value.equalsIgnoreCase(value)) {
                return criticality;
            }
        }
        throw new IllegalArgumentException("Invalid AssetCriticality: " + value);
    }
    
}
