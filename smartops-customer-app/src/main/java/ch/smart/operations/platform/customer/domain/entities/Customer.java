package ch.smart.operations.platform.customer.domain.entities;

import java.util.UUID;

import ch.smart.operations.platform.customer.domain.enums.CustomerStatus;


public class Customer {

    private UUID id;
    private String customerNumber;
    private String legalName;
    private String displayName;
    private CustomerStatus status;
    private String taxIdentifier;
    private String industry;
    private String notes;


    public Customer(UUID id, String customerNumber, String legalName, String displayName, CustomerStatus status, String taxIdentifier, String industry, String notes) {
        this.id = id;
        this.customerNumber = customerNumber;
        this.legalName = legalName;
        this.displayName = displayName;
        this.status = status;
        this.taxIdentifier = taxIdentifier;
        this.industry = industry;
        this.notes = notes;
    }

    public static Customer create(String customerNumber, String legalName, String displayName, String taxIdentifier, String industry, String notes){
        return new Customer(UUID.randomUUID(), customerNumber, legalName, displayName, CustomerStatus.ACTIVE, taxIdentifier, industry, notes);
    }


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
