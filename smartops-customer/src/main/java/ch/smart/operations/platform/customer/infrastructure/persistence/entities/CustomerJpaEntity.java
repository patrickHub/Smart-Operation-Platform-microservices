package ch.smart.operations.platform.customer.infrastructure.persistence.entities;



import java.util.UUID;

import ch.smart.operations.platform.customer.domain.enums.CustomerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers", schema = "customer")
public class CustomerJpaEntity {


    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name="customer_number", unique = true, nullable = false)
    private String customerNumber;

    @Column(name="legal_name", nullable = false)
    private String legalName;

    @Column(name="display_name", nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private CustomerStatus status;


    @Column(name="tax_identifier", nullable = false)
    private String taxIdentifier;

    @Column(name="industry", nullable = false)
    private String industry;

     @Column(name="notes", nullable = false, length = 2000)
    private String notes;


    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCustomerNumber() {
        return this.customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getLegalName() {
        return this.legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public CustomerStatus getStatus() {
        return this.status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public String getTaxIdentifier() {
        return this.taxIdentifier;
    }

    public void setTaxIdentifier(String taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
    }

    public String getIndustry() {
        return this.industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    

    
}