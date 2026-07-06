package ch.smart.operations.platform.workorder.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkOrderTaskStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    DONE("Done"),
    SKIPPED("Skipped");


    private final String value;

    WorkOrderTaskStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static WorkOrderTaskStatus fromValue(String value) {
        for (WorkOrderTaskStatus type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
}
