package ch.smart.operations.platform.identity.application.services;


import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

@Service
public class IdentityApplicationService {

    private final IdentityUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public IdentityApplicationService(
            IdentityUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequest request) {
        IdentityUserJpaEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> invalidCredentials());

        if (!user.isActive()) {
            throw invalidCredentials();
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }

        String token = jwtTokenService.generateAccessToken(user);

        return new LoginResponseDto(
                token,
                "Bearer",
                jwtTokenService.getAccessTokenValiditySeconds(),
                user.getUsername(),
                user.getRoles()
                        .stream()
                        .map(Enum::name)
                        .toList()
        );
    }

    public UserResponse createUser(CreateUserRequest request) {
        Map<String, String[]> errors = new LinkedHashMap<>();

        if (userRepository.existsByUsername(request.username())) {
            errors.put("username", new String[]{"Username already exists"});
        }

        if (userRepository.existsByEmail(request.email())) {
            errors.put("email", new String[]{"Email already exists"});
        }

        Set<UserRole> roles = parseRoles(request.roles(), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        OffsetDateTime now = OffsetDateTime.now();

        IdentityUserJpaEntity user = new IdentityUserJpaEntity(
                UUID.randomUUID(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.displayName(),
                request.email(),
                IdentityUserStatus.ACTIVE,
                roles,
                now,
                now
        );

        IdentityUserJpaEntity savedUser = userRepository.save(user);

        return toUserResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String username, String email){
        List<IdentityUserJpaEntity> users;
        if(username != null){
            users = userRepository.findByUsername(username).stream().toList();
        }else if (email != null){
            users = userRepository.findByEmail(email).stream().toList();
        }
        else{
            users = userRepository.findAll();
        }

        return users.stream().map(this::toUserResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username){
        IdentityUserJpaEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User could not be found by username: " + username));
        
        return toUserResponse(user);
    }


    private Set<UserRole> parseRoles(Set<String> roleValues, Map<String, String[]> errors) {
        Set<UserRole> roles = new LinkedHashSet<>();

        for (String roleValue : roleValues) {
            try {
                roles.add(UserRole.valueOf(roleValue));
            } catch (IllegalArgumentException ex) {
                errors.put("roles", new String[]{
                        "Invalid role: " + roleValue + ". Allowed roles are: ADMIN, SUPPORT_AGENT, DISPATCHER, TECHNICIAN, BILLING_MANAGER"
                });
            }
        }

        return roles;
    }

    private UserResponse toUserResponse(IdentityUserJpaEntity user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getStatus().name(),
                user.getRoles()
                        .stream()
                        .map(Enum::name)
                        .toList(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }


    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password"
        );
    }
}