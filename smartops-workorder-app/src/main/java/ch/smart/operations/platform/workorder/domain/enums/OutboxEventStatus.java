package ch.smart.operations.platform.workorder.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OutboxEventStatus {
    PENDING("Pending"),
    PUBLISHED("Published"),
    FAILED("Failed");

    private final String value;

    OutboxEventStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OutboxEventStatus fromValue(String value) {
        for (OutboxEventStatus type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
}
