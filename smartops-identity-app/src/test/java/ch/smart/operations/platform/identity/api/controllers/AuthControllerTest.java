package ch.smart.operations.platform.identity.api.controllers;

import ch.smart.operations.platform.identity.api.contracts.CreateUserRequest;
import ch.smart.operations.platform.identity.api.contracts.LoginRequest;
import ch.smart.operations.platform.identity.application.dtos.LoginResponseDto;
import ch.smart.operations.platform.identity.application.dtos.UserResponse;
import ch.smart.operations.platform.identity.application.services.IdentityApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;
    private IdentityApplicationService identityApplicationService;

    @BeforeEach
    void setUp() {
        identityApplicationService = mock(IdentityApplicationService.class);
        AuthController authController = new AuthController(identityApplicationService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
        LoginResponseDto response = new LoginResponseDto(
                "jwt-token",
                "Bearer",
                3600L,
                "admin",
                List.of("ADMIN")
        );

        when(identityApplicationService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "admin123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"));
    }

    @Test
    void createUser_shouldReturnCreatedUser_whenRequestIsValid() throws Exception {
        UserResponse response = userResponse(
                "john",
                "John",
                "Doe",
                "John Doe",
                "john.doe@smartops.local",
                "Support Agent",
                List.of("SUPPORT_AGENT")
        );

        when(identityApplicationService.createUser(any(CreateUserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john",
                                  "password": "john12345",
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "displayName": "John Doe",
                                  "email": "john.doe@smartops.local",
                                  "function": "Support Agent",
                                  "roles": ["SUPPORT_AGENT"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.displayName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@smartops.local"))
                .andExpect(jsonPath("$.function").value("Support Agent"))
                .andExpect(jsonPath("$.roles[0]").value("SUPPORT_AGENT"));
    }

    @Test
    void searchUsers_shouldReturnUsers_whenNoFilterIsProvided() throws Exception {
        UserResponse admin = userResponse(
                "admin",
                "Patrick",
                "Djomo",
                "Patrick Djomo",
                "admin@smartops.local",
                "Platform Administrator",
                List.of("ADMIN")
        );

        when(identityApplicationService.searchUsers(null, null))
                .thenReturn(List.of(admin));

        mockMvc.perform(get("/api/v1/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].function").value("Platform Administrator"));
    }

    @Test
    void searchUsers_shouldPassUsernameFilterToService() throws Exception {
        UserResponse admin = userResponse(
                "admin",
                "Patrick",
                "Djomo",
                "Patrick Djomo",
                "admin@smartops.local",
                "Platform Administrator",
                List.of("ADMIN")
        );

        when(identityApplicationService.searchUsers("admin", null))
                .thenReturn(List.of(admin));

        mockMvc.perform(get("/api/v1/auth/users")
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    @Test
    void getUserByUsername_shouldReturnUser() throws Exception {
        UserResponse admin = userResponse(
                "admin",
                "Patrick",
                "Djomo",
                "Patrick Djomo",
                "admin@smartops.local",
                "Platform Administrator",
                List.of("ADMIN")
        );

        when(identityApplicationService.getUserByUsername(eq("admin")))
                .thenReturn(admin);

        mockMvc.perform(get("/api/v1/auth/users/username/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.displayName").value("Patrick Djomo"))
                .andExpect(jsonPath("$.function").value("Platform Administrator"));
    }

    private UserResponse userResponse(
            String username,
            String firstName,
            String lastName,
            String displayName,
            String email,
            String function,
            List<String> roles
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        return new UserResponse(
                UUID.randomUUID(),
                username,
                firstName,
                lastName,
                displayName,
                email,
                function,
                "ACTIVE",
                roles,
                now,
                now
        );
    }
}