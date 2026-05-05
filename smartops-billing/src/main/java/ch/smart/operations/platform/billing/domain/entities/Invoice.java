package ch.smart.operations.platform.billing.domain.entities;

import ch.smart.operations.platform.billing.domain.enums.InvoiceStatus;

import ch.smart.operations.platform.shared.exceptions.BusinessRuleException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Invoice {

    private final UUID id;
    private final String invoiceNumber;
    private final UUID workOrderId;
    private final UUID customerId;
    private final UUID pricingPolicyId;
    private final InvoiceStatus status;
    private final String currency;
    private final BigDecimal subtotalAmount;
    private final BigDecimal taxAmount;
    private final BigDecimal totalAmount;
    private final OffsetDateTime generatedAt;
    private final LocalDate dueDate;
    private final Long version;
    private final OffsetDateTime createdAt;
    private final String createdBy;
    private final OffsetDateTime updatedAt;
    private final String updatedBy;

    public Invoice(
            UUID id,
            String invoiceNumber,
            UUID workOrderId,
            UUID customerId,
            UUID pricingPolicyId,
            InvoiceStatus status,
            String currency,
            BigDecimal subtotalAmount,
            BigDecimal taxAmount,
            BigDecimal totalAmount,
            OffsetDateTime generatedAt,
            LocalDate dueDate,
            Long version,
            OffsetDateTime createdAt,
            String createdBy,
            OffsetDateTime updatedAt,
            String updatedBy
    ) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.workOrderId = workOrderId;
        this.customerId = customerId;
        this.pricingPolicyId = pricingPolicyId;
        this.status = status;
        this.currency = currency;
        this.subtotalAmount = subtotalAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.generatedAt = generatedAt;
        this.dueDate = dueDate;
        this.version = version;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public static Invoice generated(
            String invoiceNumber,
            UUID workOrderId,
            UUID customerId,
            UUID pricingPolicyId,
            String currency,
            BigDecimal subtotalAmount,
            BigDecimal taxAmount,
            BigDecimal totalAmount,
            LocalDate dueDate,
            String createdBy
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        return new Invoice(
                UUID.randomUUID(),
                invoiceNumber,
                workOrderId,
                customerId,
                pricingPolicyId,
                InvoiceStatus.GENERATED,
                currency,
                subtotalAmount,
                taxAmount,
                totalAmount,
                now,
                dueDate,
                null,
                now,
                createdBy,
                now,
                createdBy
        );
    }

    public Invoice markSent(String updatedBy) {
        if (status != InvoiceStatus.GENERATED) {
            throw new BusinessRuleException(
                    "Only GENERATED invoices can be marked as SENT."
            );
        }

        return copyWithStatus(InvoiceStatus.SENT, updatedBy);
    }

    public Invoice cancel(String updatedBy) {
        if (status == InvoiceStatus.PAID) {
            throw new BusinessRuleException(
                    "Paid invoices cannot be cancelled."
            );
        }

        if (status == InvoiceStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "Invoice is already cancelled."
            );
        }

        return copyWithStatus(InvoiceStatus.CANCELLED, updatedBy);
    }

    private Invoice copyWithStatus(InvoiceStatus newStatus, String updatedBy) {
        return new Invoice(
                id,
                invoiceNumber,
                workOrderId,
                customerId,
                pricingPolicyId,
                newStatus,
                currency,
                subtotalAmount,
                taxAmount,
                totalAmount,
                generatedAt,
                dueDate,
                version,
                createdAt,
                createdBy,
                OffsetDateTime.now(),
                updatedBy
        );
    }

    public UUID getId() { return id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public UUID getWorkOrderId() { return workOrderId; }
    public UUID getCustomerId() { return customerId; }
    public UUID getPricingPolicyId() { return pricingPolicyId; }
    public InvoiceStatus getStatus() { return status; }
    public String getCurrency() { return currency; }
    public BigDecimal getSubtotalAmount() { return subtotalAmount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OffsetDateTime getGeneratedAt() { return generatedAt; }
    public LocalDate getDueDate() { return dueDate; }
    public Long getVersion() { return version; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
}