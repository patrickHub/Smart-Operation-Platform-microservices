package ch.smart.operations.platform.billing.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum InvoiceStatus {
    DRAFT("Draft"),
    GENERATED("Generated"),
    SENT("Sent"),
    CANCELLED("Cancelled"),
    PAID("Paid");

    private final String value;

    InvoiceStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InvoiceStatus fromValue(String value) {
        for (InvoiceStatus type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
}