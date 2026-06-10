package ch.smart.operations.platform.customer.infrastructure.persistence.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import ch.smart.operations.platform.customer.domain.enums.CustomerSiteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "customer_sites", schema = "customer")
public class CustomerSiteJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "site_number", nullable = false, unique = true)
    private String siteNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "site_name", nullable = false)
    private String siteName ;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CustomerSiteStatus status;

    @Column(name = "address_line_1", nullable = false)
    private String addressLine1 ;

    @Column(name = "address_line_2", nullable = true)
    private String addressLine2 ;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state_region", nullable = false)
    private String stateRegion;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @Column(name = "timezone", nullable = false)
    private String timezone;

    @Column(name = "access_instructions", nullable = true, length = 2000)
    private String accessInstructions;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;


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
