package ch.smart.operations.platform.workorder.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AssignmentStatus {
    ASSIGNED("Assigned"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    UNASSIGNED("Unassigned");

    private final String value;

    AssignmentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AssignmentStatus fromValue(String value) {
        for (AssignmentStatus type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
}