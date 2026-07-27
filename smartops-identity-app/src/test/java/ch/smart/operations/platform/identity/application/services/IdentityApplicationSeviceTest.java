package ch.smart.operations.platform.identity.application.services;

import ch.smart.operations.platform.identity.api.contracts.CreateUserRequest;
import ch.smart.operations.platform.identity.api.contracts.LoginRequest;
import ch.smart.operations.platform.identity.application.dtos.LoginResponseDto;
import ch.smart.operations.platform.identity.application.dtos.UserResponse;
import ch.smart.operations.platform.identity.domain.enums.IdentityUserStatus;
import ch.smart.operations.platform.identity.domain.enums.UserRole;
import ch.smart.operations.platform.identity.infrastructure.persistence.entities.IdentityUserJpaEntity;
import ch.smart.operations.platform.identity.infrastructure.persistence.repositories.IdentityUserRepository;
import ch.smart.operations.platform.shared.exceptions.NotFoundException;
import ch.smart.operations.platform.shared.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IdentityApplicationServiceTest {

    private IdentityUserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenService jwtTokenService;
    private IdentityApplicationService identityApplicationService;

    @BeforeEach
    void setUp() {
        userRepository = mock(IdentityUserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtTokenService = mock(JwtTokenService.class);

        identityApplicationService = new IdentityApplicationService(
                userRepository,
                passwordEncoder,
                jwtTokenService
        );
    }

    @Test
    void login_shouldReturnAccessToken_whenCredentialsAreValid() {
        String passwordHash = "$2a$10$XZreOR34tju522j4n7Vrb.ujNxAqQ7kicPZvMGk3m.fujIzNSliQK"; // bcrypt hash for "admin123"
        // Arrange
        IdentityUserJpaEntity user = activeUser(
                "admin",
                passwordHash,
                Set.of(UserRole.ADMIN)
        );

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("admin123", passwordHash)).thenReturn(true);
        when(jwtTokenService.generateAccessToken(user)).thenReturn("jwt-token");
        when(jwtTokenService.getAccessTokenValiditySeconds()).thenReturn(3600L);

        LoginRequest request = new LoginRequest("admin", "admin123");

        // Act
        LoginResponseDto response = identityApplicationService.login(request);

        // Assert
        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600L);
        assertThat(response.username()).isEqualTo("admin");
        assertThat(response.roles()).containsExactly("ADMIN");

        verify(userRepository).findByUsername("admin");
        verify(passwordEncoder).matches("admin123", passwordHash);
        verify(jwtTokenService).generateAccessToken(user);
    }

    @Test
    void login_shouldThrowUnauthorized_whenUsernameDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("unknown", "password");

        // Act / Assert
        assertThatThrownBy(() -> identityApplicationService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401 UNAUTHORIZED");

        verify(userRepository).findByUsername("unknown");
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtTokenService);
    }

    @Test
    void login_shouldThrowUnauthorized_whenPasswordIsInvalid() {
        // Arrange
        IdentityUserJpaEntity user = activeUser(
                "admin",
                "encoded-password",
                Set.of(UserRole.ADMIN)
        );

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        LoginRequest request = new LoginRequest("admin", "wrong-password");

        // Act / Assert
        assertThatThrownBy(() -> identityApplicationService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401 UNAUTHORIZED");

        verify(userRepository).findByUsername("admin");
        verify(passwordEncoder).matches("wrong-password", "encoded-password");
        verifyNoInteractions(jwtTokenService);
    }

    @Test
    void createUser_shouldCreateUser_whenRequestIsValid() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "john",
                "john12345",
                "John",
                "Doe",
                "John Doe",
                "john.doe@smartops.local",
                "Support Agent",
                Set.of("SUPPORT_AGENT")
        );

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john.doe@smartops.local")).thenReturn(false);
        when(passwordEncoder.encode("john12345")).thenReturn("encoded-password");

        when(userRepository.save(any(IdentityUserJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse response = identityApplicationService.createUser(request);

        // Assert
        assertThat(response.username()).isEqualTo("john");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.displayName()).isEqualTo("John Doe");
        assertThat(response.email()).isEqualTo("john.doe@smartops.local");
        assertThat(response.function()).isEqualTo("Support Agent");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.roles()).containsExactly("SUPPORT_AGENT");

        verify(userRepository).existsByUsername("john");
        verify(userRepository).existsByEmail("john.doe@smartops.local");
        verify(passwordEncoder).encode("john12345");
        verify(userRepository).save(any(IdentityUserJpaEntity.class));
    }

    @Test
    void createUser_shouldThrowValidationException_whenUsernameAlreadyExists() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "admin",
                "admin12345",
                "Admin",
                "User",
                "Admin User",
                "admin.user@smartops.local",
                "Platform Administrator",
                Set.of("ADMIN")
        );

        when(userRepository.existsByUsername("admin")).thenReturn(true);
        when(userRepository.existsByEmail("admin.user@smartops.local")).thenReturn(false);

        // Act / Assert
        assertThatThrownBy(() -> identityApplicationService.createUser(request))
                .isInstanceOf(ValidationException.class);

        verify(userRepository).existsByUsername("admin");
        verify(userRepository).existsByEmail("admin.user@smartops.local");
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldThrowValidationException_whenEmailAlreadyExists() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "john",
                "john12345",
                "John",
                "Doe",
                "John Doe",
                "existing@smartops.local",
                "Support Agent",
                Set.of("SUPPORT_AGENT")
        );

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("existing@smartops.local")).thenReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> identityApplicationService.createUser(request))
                .isInstanceOf(ValidationException.class);

        verify(userRepository).existsByUsername("john");
        verify(userRepository).existsByEmail("existing@smartops.local");
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldThrowValidationException_whenRoleIsInvalid() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "john",
                "john12345",
                "John",
                "Doe",
                "John Doe",
                "john.doe@smartops.local",
                "Unknown Function",
                Set.of("UNKNOWN_ROLE")
        );

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john.doe@smartops.local")).thenReturn(false);

        // Act / Assert
        assertThatThrownBy(() -> identityApplicationService.createUser(request))
                .isInstanceOf(ValidationException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void searchUsers_shouldReturnUserByUsername_whenUsernameIsProvided() {
        // Arrange
        IdentityUserJpaEntity user = activeUser(
                "admin",
                "encoded-password",
                Set.of(UserRole.ADMIN)
        );

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // Act
        List<UserResponse> response = identityApplicationService.searchUsers("admin", null);

        // Assert
        assertThat(response).hasSize(1);
        assertThat(response.getFirst().username()).isEqualTo("admin");

        verify(userRepository).findByUsername("admin");
        verify(userRepository, never()).findAll();
    }

    @Test
    void searchUsers_shouldReturnUserByEmail_whenEmailIsProvidedAndUsernameIsNull() {
        // Arrange
        IdentityUserJpaEntity user = activeUser(
                "admin",
                "encoded-password",
                Set.of(UserRole.ADMIN)
        );

        when(userRepository.findByEmail("admin@smartops.local")).thenReturn(Optional.of(user));

        // Act
        List<UserResponse> response = identityApplicationService.searchUsers(null, "admin@smartops.local");

        // Assert
        assertThat(response).hasSize(1);
        assertThat(response.getFirst().email()).isEqualTo("admin@smartops.local");

        verify(userRepository).findByEmail("admin@smartops.local");
        verify(userRepository, never()).findAll();
    }

    @Test
    void searchUsers_shouldReturnAllUsers_whenNoFilterIsProvided() {
        // Arrange
        IdentityUserJpaEntity admin = activeUser(
                "admin",
                "encoded-password",
                Set.of(UserRole.ADMIN)
        );

        IdentityUserJpaEntity support = activeUser(
                "support",
                "encoded-password",
                Set.of(UserRole.SUPPORT_AGENT)
        );

        when(userRepository.findAll()).thenReturn(List.of(admin, support));

        // Act
        List<UserResponse> response = identityApplicationService.searchUsers(null, null);

        // Assert
        assertThat(response).hasSize(2);
        assertThat(response).extracting(UserResponse::username)
                .containsExactly("admin", "support");

        verify(userRepository).findAll();
    }

    @Test
    void getUserByUsername_shouldReturnUser_whenUserExists() {
        // Arrange
        IdentityUserJpaEntity user = activeUser(
                "admin",
                "encoded-password",
                Set.of(UserRole.ADMIN)
        );

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // Act
        UserResponse response = identityApplicationService.getUserByUsername("admin");

        // Assert
        assertThat(response.username()).isEqualTo("admin");
        assertThat(response.roles()).containsExactly("ADMIN");

        verify(userRepository).findByUsername("admin");
    }

    @Test
    void getUserByUsername_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> identityApplicationService.getUserByUsername("missing"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User could not be found by username: missing");

        verify(userRepository).findByUsername("missing");
    }

    private IdentityUserJpaEntity activeUser(String username, String passwordHash, Set<UserRole> roles) {
        OffsetDateTime now = OffsetDateTime.now();

        return new IdentityUserJpaEntity(
                UUID.randomUUID(),
                username,
                passwordHash,
                resolveFirstName(username),
                resolveLastName(username),
                resolveDisplayName(username),
                username + "@smartops.local",
                resolveFunction(username),
                IdentityUserStatus.ACTIVE,
                new LinkedHashSet<>(roles),
                now,
                now
        );
    }

    private String resolveFirstName(String username) {
        return switch (username) {
            case "admin" -> "Patrick";
            case "support" -> "Support";
            default -> "Test";
        };
    }

    private String resolveLastName(String username) {
        return switch (username) {
            case "admin" -> "Djomo";
            case "support" -> "Agent";
            default -> "User";
        };
    }

    private String resolveDisplayName(String username) {
        return resolveFirstName(username) + " " + resolveLastName(username);
    }

    private String resolveFunction(String username) {
        return switch (username) {
            case "admin" -> "Platform Administrator";
            case "support" -> "Support Agent";
            default -> "Authenticated User";
        };
    }
}