package ch.smart.operations.platform.identity.infrastructure.persistence.entities;

import ch.smart.operations.platform.identity.domain.enums.IdentityUserStatus;
import ch.smart.operations.platform.identity.domain.enums.UserRole;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "identity")
public class IdentityUserJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "job_function", nullable = false, length = 150)
    private String function;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private IdentityUserStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            schema = "identity",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 100)
    private Set<UserRole> roles = new LinkedHashSet<>();

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected IdentityUserJpaEntity() {
    }

    public IdentityUserJpaEntity(
            UUID id,
            String username,
            String passwordHash,
            String firstName,
            String lastName,
            String displayName,
            String email,
            String function,
            IdentityUserStatus status,
            Set<UserRole> roles,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.function = function;
        this.displayName = displayName;
        this.email = email;
        this.status = status;
        this.roles = roles == null ? new LinkedHashSet<>() : new LinkedHashSet<>(roles);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
    return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFunction() {
        return function;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public IdentityUserStatus getStatus() {
        return status;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public boolean isActive() {
        return IdentityUserStatus.ACTIVE.equals(status);
    }
}
