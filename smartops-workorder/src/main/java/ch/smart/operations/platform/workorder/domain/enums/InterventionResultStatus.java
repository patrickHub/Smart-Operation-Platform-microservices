package ch.smart.operations.platform.workorder.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InterventionResultStatus {
    SUCCESS("Success"),
    PARTIAL_SUCCESS("Partial Success"),
    FAILED("Failed");

    private final String value;

    InterventionResultStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InterventionResultStatus fromValue(String value) {
        for (InterventionResultStatus type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
}
