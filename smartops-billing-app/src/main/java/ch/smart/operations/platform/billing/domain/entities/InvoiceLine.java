package ch.smart.operations.platform.billing.domain.entities;

import ch.smart.operations.platform.billing.domain.enums.InvoiceLineType;

import java.math.BigDecimal;
import java.util.UUID;

public class InvoiceLine {

    private final UUID id;
    private final UUID invoiceId;
    private final int lineNumber;
    private final InvoiceLineType lineType;
    private final String description;
    private final BigDecimal quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal lineTotal;

    public InvoiceLine(
            UUID id,
            UUID invoiceId,
            int lineNumber,
            InvoiceLineType lineType,
            String description,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.lineNumber = lineNumber;
        this.lineType = lineType;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    public static InvoiceLine of(
            UUID invoiceId,
            int lineNumber,
            InvoiceLineType lineType,
            String description,
            BigDecimal quantity,
            BigDecimal unitPrice
    ) {
        return new InvoiceLine(
                UUID.randomUUID(),
                invoiceId,
                lineNumber,
                lineType,
                description,
                quantity,
                unitPrice,
                quantity.multiply(unitPrice)
        );
    }

    public UUID getId() { return id; }
    public UUID getInvoiceId() { return invoiceId; }
    public int getLineNumber() { return lineNumber; }
    public InvoiceLineType getLineType() { return lineType; }
    public String getDescription() { return description; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getLineTotal() { return lineTotal; }
}