package ch.smart.operations.platform.billing.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum InvoiceLineType {
    LABOR("Labor"),
    PART("Part"),
    SERVICE_FEE("Service Fee");

    private final String value;

    InvoiceLineType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InvoiceLineType fromValue(String value) {
        for (InvoiceLineType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
}
