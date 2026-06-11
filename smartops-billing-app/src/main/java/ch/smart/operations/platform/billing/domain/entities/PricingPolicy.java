package ch.smart.operations.platform.billing.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PricingPolicy {

    private final UUID id;
    private final String code;
    private final String name;
    private final String workType;
    private final BigDecimal laborRate;
    private final String defaultCurrency;
    private final boolean active;
    private final LocalDate validFrom;
    private final LocalDate validTo;
    private final String rulesJson;
    private final OffsetDateTime createdAt;
    private final String createdBy;
    private final OffsetDateTime updatedAt;
    private final String updatedBy;

    public PricingPolicy(
            UUID id,
            String code,
            String name,
            String workType,
            BigDecimal laborRate,
            String defaultCurrency,
            boolean active,
            LocalDate validFrom,
            LocalDate validTo,
            String rulesJson,
            OffsetDateTime createdAt,
            String createdBy,
            OffsetDateTime updatedAt,
            String updatedBy
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.workType = workType;
        this.laborRate = laborRate;
        this.defaultCurrency = defaultCurrency;
        this.active = active;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.rulesJson = rulesJson;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getWorkType() { return workType; }
    public BigDecimal getLaborRate() { return laborRate; }
    public String getDefaultCurrency() { return defaultCurrency; }
    public boolean isActive() { return active; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public String getRulesJson() { return rulesJson; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
}
