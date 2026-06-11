package ch.smart.operations.platform.workorder.domain.entities;

import java.math.BigDecimal;
import java.util.UUID;

public class UsedPart {

    private final UUID id;
    private final UUID interventionReportId;
    private final String partNumber;
    private final String partName;
    private final BigDecimal quantity;
    private final BigDecimal unitPrice;
    private final String currency;

    public UsedPart(
            UUID id,
            UUID interventionReportId,
            String partNumber,
            String partName,
            BigDecimal quantity,
            BigDecimal unitPrice,
            String currency
    ) {
        this.id = id;
        this.interventionReportId = interventionReportId;
        this.partNumber = partNumber;
        this.partName = partName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.currency = currency;
    }

    public UUID getId() { return id; }
    public UUID getInterventionReportId() { return interventionReportId; }
    public String getPartNumber() { return partNumber; }
    public String getPartName() { return partName; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getCurrency() { return currency; }
}