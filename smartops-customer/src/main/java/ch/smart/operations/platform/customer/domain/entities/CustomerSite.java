package ch.smart.operations.platform.customer.domain.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import ch.smart.operations.platform.customer.domain.enums.CustomerSiteStatus;

public class CustomerSite {

    private UUID id;
    private String siteNumber;
    private UUID customerId;
    private String siteName ;
    private CustomerSiteStatus status;
    private String addressLine1 ;
    private String addressLine2 ;
    private String city;
    private String stateRegion;
    private String postalCode;
    private String countryCode;
    private String timezone;
    private String accessInstructions;
    private Long version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;


    public CustomerSite(UUID id, String siteNumber, UUID customerId, String siteName, CustomerSiteStatus status, String addressLine1, String addressLine2, String city, String stateRegion, String postalCode, String countryCode, String timezone, String accessInstructions, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.siteNumber = siteNumber;
        this.customerId = customerId;
        this.siteName = siteName;
        this.status = status;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.stateRegion = stateRegion;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
        this.timezone = timezone;
        this.accessInstructions = accessInstructions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CustomerSite create(String siteNumber, UUID customerId, String siteName, String addressLine1, String addressLine2, String city, String stateRegion, String postalCode, String countryCode, String timezone, String accessInstructions) {
        return new CustomerSite(
            UUID.randomUUID(),
            siteNumber,
            customerId,
            siteName,           
            CustomerSiteStatus.ACTIVE,
            addressLine1,
            addressLine2,
            city,
            stateRegion,
            postalCode,
            countryCode, 
            timezone, 
            accessInstructions, 
            OffsetDateTime.now(), 
            OffsetDateTime.now()
        );
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSiteNumber() {
        return this.siteNumber;
    }

    public void setSiteNumber(String siteNumber) {
        this.siteNumber = siteNumber;
    }

    public UUID getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }


    public CustomerSiteStatus getStatus() {
        return this.status;
    }

    public void setStatus(CustomerSiteStatus status) {
        this.status = status;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateRegion() {
        return this.stateRegion;
    }

    public void setStateRegion(String stateRegion) {
        this.stateRegion = stateRegion;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getAccessInstructions() {
        return this.accessInstructions;
    }

    public void setAccessInstructions(String accessInstructions) {
        this.accessInstructions = accessInstructions;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


}
