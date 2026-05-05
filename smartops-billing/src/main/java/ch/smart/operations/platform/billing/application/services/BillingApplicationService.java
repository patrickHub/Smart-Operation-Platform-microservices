package ch.smart.operations.platform.billing.application.services;

import ch.smart.operations.platform.billing.application.commands.GenerateInvoiceFromWorkOrderCommand;
import ch.smart.operations.platform.billing.application.commands.InvoiceActionCommand;
import ch.smart.operations.platform.billing.application.dtos.*;
import ch.smart.operations.platform.billing.application.ports.*;
import ch.smart.operations.platform.billing.domain.entities.BillingOutboxEvent;
import ch.smart.operations.platform.billing.domain.entities.Invoice;
import ch.smart.operations.platform.billing.domain.entities.InvoiceLine;
import ch.smart.operations.platform.billing.domain.entities.PricingPolicy;
import ch.smart.operations.platform.billing.domain.enums.InvoiceLineType;
import ch.smart.operations.platform.billing.domain.enums.InvoiceStatus;
import ch.smart.operations.platform.shared.exceptions.BusinessRuleException;
import ch.smart.operations.platform.shared.exceptions.NotFoundException;
import ch.smart.operations.platform.shared.exceptions.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.time.OffsetDateTime;

@Service
@Transactional
public class BillingApplicationService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.081"); // Swiss VAT

    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final PricingPolicyRepository pricingPolicyRepository;
    private final WorkOrderReferencePort workOrderReferencePort;
    private final BillingOutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public BillingApplicationService(
            InvoiceRepository invoiceRepository,
            InvoiceLineRepository invoiceLineRepository,
            PricingPolicyRepository pricingPolicyRepository,
            WorkOrderReferencePort workOrderReferencePort,
            BillingOutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper
    ) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.pricingPolicyRepository = pricingPolicyRepository;
        this.workOrderReferencePort = workOrderReferencePort;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    public UUID generateInvoiceFromWorkOrder(GenerateInvoiceFromWorkOrderCommand command) {
        validateGenerateCommand(command);

        if (invoiceRepository.existsByWorkOrderId(command.workOrderId())) {
            throw new BusinessRuleException("Invoice already exists for work order " + command.workOrderId());
        }

        WorkOrderBillingSummaryDto workOrder = workOrderReferencePort.findBillingSummary(command.workOrderId())
                .orElseThrow(() -> new NotFoundException("Work order not found with id " + command.workOrderId()));

        if (!"COMPLETED".equalsIgnoreCase(workOrder.status())) {
            throw new BusinessRuleException("Invoice can only be generated for COMPLETED work orders.");
        }

        PricingPolicy pricingPolicy = pricingPolicyRepository
                .findActiveByWorkType(workOrder.type(), LocalDate.now())
                .orElseThrow(() -> new BusinessRuleException("No active pricing policy found for work type " + workOrder.type()));

        BigDecimal laborQuantity = workOrder.laborDurationMinutes() == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(workOrder.laborDurationMinutes())
                    .divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);

        List<InvoiceLine> linesToCreate = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        UUID invoiceId = UUID.randomUUID();

        if (laborQuantity.compareTo(BigDecimal.ZERO) > 0) {
            InvoiceLine laborLine = InvoiceLine.of(
                    invoiceId,
                    1,
                    InvoiceLineType.LABOR,
                    "Labor for work order " + workOrder.workOrderNumber(),
                    laborQuantity,
                    pricingPolicy.getLaborRate()
            );
            linesToCreate.add(laborLine);
            subtotal = subtotal.add(laborLine.getLineTotal());
        }

        int lineNumber = linesToCreate.size() + 1;

        for (UsedPartBillingDto part : workOrder.usedParts()) {
            BigDecimal unitPrice = part.unitPrice() != null ? part.unitPrice() : BigDecimal.ZERO;

            InvoiceLine partLine = InvoiceLine.of(
                    invoiceId,
                    lineNumber++,
                    InvoiceLineType.PART,
                    part.partName() + " (" + part.partNumber() + ")",
                    part.quantity(),
                    unitPrice
            );

            linesToCreate.add(partLine);
            subtotal = subtotal.add(partLine.getLineTotal());
        }

        BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        Invoice invoice = new Invoice(
                invoiceId,
                generateInvoiceNumber(),
                workOrder.id(),
                workOrder.customerId(),
                pricingPolicy.getId(),
                InvoiceStatus.GENERATED,
                pricingPolicy.getDefaultCurrency(),
                subtotal,
                taxAmount,
                totalAmount,
                OffsetDateTime.now(),
                command.dueDate(),
                null,
                OffsetDateTime.now(),
                command.createdBy().trim(),
                OffsetDateTime.now(),
                command.createdBy().trim()
        );

        Invoice saved = invoiceRepository.save(invoice);

        linesToCreate.forEach(invoiceLineRepository::save);

        saveOutboxEvent(saved, "INVOICE_GENERATED");

        return saved.getId();
    }

    @Transactional(readOnly = true)
    public InvoiceDto getInvoice(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice not found with id " + invoiceId));

        return toDto(invoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceDto> searchInvoices(UUID customerId, UUID workOrderId, InvoiceStatus status) {
        List<Invoice> invoices;

        if (customerId != null) {
            invoices = invoiceRepository.findByCustomerId(customerId);
        } else if (workOrderId != null) {
            invoices = invoiceRepository.findByWorkOrderIdAsList(workOrderId);
        } else if (status != null) {
            invoices = invoiceRepository.findByStatus(status);
        } else {
            invoices = invoiceRepository.findAll();
        }

        return invoices.stream().map(this::toDto).toList();
    }

    public void markInvoiceAsSent(InvoiceActionCommand command) {
        Invoice invoice = invoiceRepository.findById(command.invoiceId())
                .orElseThrow(() -> new NotFoundException("Invoice not found with id " + command.invoiceId()));

        Invoice sent = invoice.markSent(command.updatedBy().trim());
        invoiceRepository.save(sent);
    }

    public void cancelInvoice(InvoiceActionCommand command) {
        Invoice invoice = invoiceRepository.findById(command.invoiceId())
                .orElseThrow(() -> new NotFoundException("Invoice not found with id " + command.invoiceId()));

        Invoice cancelled = invoice.cancel(command.updatedBy().trim());
        invoiceRepository.save(cancelled);
    }

    private InvoiceDto toDto(Invoice invoice) {
        List<InvoiceLineDto> lines = invoiceLineRepository.findByInvoiceId(invoice.getId())
                .stream()
                .map(this::toLineDto)
                .toList();

        return new InvoiceDto(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getWorkOrderId(),
                invoice.getCustomerId(),
                invoice.getPricingPolicyId(),
                invoice.getStatus().name(),
                invoice.getCurrency(),
                invoice.getSubtotalAmount(),
                invoice.getTaxAmount(),
                invoice.getTotalAmount(),
                invoice.getGeneratedAt(),
                invoice.getDueDate(),
                invoice.getCreatedAt(),
                invoice.getCreatedBy(),
                invoice.getUpdatedAt(),
                invoice.getUpdatedBy(),
                lines
        );
    }

    private InvoiceLineDto toLineDto(InvoiceLine line) {
        return new InvoiceLineDto(
                line.getId(),
                line.getInvoiceId(),
                line.getLineNumber(),
                line.getLineType().name(),
                line.getDescription(),
                line.getQuantity(),
                line.getUnitPrice(),
                line.getLineTotal()
        );
    }

    private void validateGenerateCommand(GenerateInvoiceFromWorkOrderCommand command) {
        Map<String, String[]> errors = new LinkedHashMap<>();

        if (command.workOrderId() == null) {
            errors.put("workOrderId", new String[]{"workOrderId is required."});
        }

        if (command.createdBy() == null || command.createdBy().isBlank()) {
            errors.put("createdBy", new String[]{"createdBy is required."});
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }


    private void saveOutboxEvent(Invoice invoice, String eventType) {
        try {

            Map<String, Object> eventData = new LinkedHashMap<>();
           eventData.put("invoiceId", invoice.getId());
            eventData.put("invoiceNumber", invoice.getInvoiceNumber());
            eventData.put("workOrderId", invoice.getWorkOrderId());
            eventData.put("customerId", invoice.getCustomerId());
            eventData.put("status", invoice.getStatus());
            eventData.put("currency", invoice.getCurrency());
            eventData.put("subtotalAmount", invoice.getSubtotalAmount());
            eventData.put("taxAmount", invoice.getTaxAmount());
            eventData.put("totalAmount", invoice.getTotalAmount());
            eventData.put("eventType", eventType);
            eventData.put("occurredAt", java.time.OffsetDateTime.now());

            String payload = objectMapper.writeValueAsString(eventData);

            outboxEventRepository.save(
                    BillingOutboxEvent.pending(invoice.getId(), eventType, payload)
            );
        } catch (Exception ex) {
            throw new BusinessRuleException("Failed to create billing outbox event.");
        }
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }
}
