package ch.smart.operations.platform.identity.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.profiles.active=test",
                "smartops.security.jwt.issuer=smartops-identity",
                "smartops.security.jwt.secret=local-dev-secret-change-me-local-dev-secret-change-me",
                "smartops.security.jwt.access-token-validity-seconds=3600",
                "spring.jpa.hibernate.ddl-auto=validate",
                "spring.flyway.enabled=true",
                "spring.flyway.locations=classpath:db/migration",
                "spring.flyway.schemas=identity",
                "spring.flyway.default-schema=identity",
                "spring.flyway.create-schemas=true",
                "spring.flyway.table=flyway_schema_history",
                "spring.jpa.properties.hibernate.default_schema=identity"
        }
)
@AutoConfigureMockMvc
class IdentityServiceIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("smartops_identity_test")
            .withUsername("smartops")
            .withPassword("smartops");

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void login_shouldReturnJwtToken_whenAdminCredentialsAreValid() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "admin123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles", hasItem("ADMIN")));
    }

    @Test
    void getUsers_shouldReturnUnauthorized_whenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/auth/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUsers_shouldReturnUsers_whenAdminTokenIsProvided() throws Exception {
        String adminToken = loginAndExtractToken("admin", "admin123");

        mockMvc.perform(get("/api/v1/auth/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.username == 'admin')]").exists())
                .andExpect(jsonPath("$[?(@.username == 'support')]").exists())
                .andExpect(jsonPath("$[?(@.username == 'dispatcher')]").exists())
                .andExpect(jsonPath("$[?(@.username == 'technician')]").exists())
                .andExpect(jsonPath("$[?(@.username == 'billing')]").exists());
    }

    @Test
    void getUsers_shouldReturnForbidden_whenTechnicianTokenIsProvided() throws Exception {
        String technicianToken = loginAndExtractToken("technician", "tech123");

        mockMvc.perform(get("/api/v1/auth/users")
                        .header("Authorization", "Bearer " + technicianToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserByUsername_shouldReturnUser_whenAuthenticatedUserHasAllowedRole() throws Exception {
        String technicianToken = loginAndExtractToken("technician", "tech123");

        mockMvc.perform(get("/api/v1/auth/users/username/admin")
                        .header("Authorization", "Bearer " + technicianToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.function").value("Platform Administrator"))
                .andExpect(jsonPath("$.roles", hasItem("ADMIN")));
    }

    @Test
    void createUser_shouldCreateUser_whenAdminTokenIsProvided() throws Exception {
        String adminToken = loginAndExtractToken("admin", "admin123");

        mockMvc.perform(post("/api/v1/auth/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "integration.user",
                                  "password": "integration123",
                                  "firstName": "Integration",
                                  "lastName": "User",
                                  "displayName": "Integration User",
                                  "email": "integration.user@smartops.local",
                                  "function": "Support Agent",
                                  "roles": ["SUPPORT_AGENT"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("integration.user"))
                .andExpect(jsonPath("$.firstName").value("Integration"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.displayName").value("Integration User"))
                .andExpect(jsonPath("$.email").value("integration.user@smartops.local"))
                .andExpect(jsonPath("$.function").value("Support Agent"))
                .andExpect(jsonPath("$.roles", hasItem("SUPPORT_AGENT")));

        mockMvc.perform(get("/api/v1/auth/users/username/integration.user")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integration.user"))
                .andExpect(jsonPath("$.email").value("integration.user@smartops.local"));
    }

    @Test
    void createUser_shouldReturnForbidden_whenSupportAgentTokenIsProvided() throws Exception {
        String supportToken = loginAndExtractToken("support", "support123");

        mockMvc.perform(post("/api/v1/auth/users")
                        .header("Authorization", "Bearer " + supportToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "forbidden.user",
                                  "password": "forbidden123",
                                  "firstName": "Forbidden",
                                  "lastName": "User",
                                  "displayName": "Forbidden User",
                                  "email": "forbidden.user@smartops.local",
                                  "function": "Support Agent",
                                  "roles": ["SUPPORT_AGENT"]
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void createUser_shouldReturnBadRequest_whenUsernameAlreadyExists() throws Exception {
        String adminToken = loginAndExtractToken("admin", "admin123");

        mockMvc.perform(post("/api/v1/auth/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "admin12345",
                                  "firstName": "Duplicate",
                                  "lastName": "Admin",
                                  "displayName": "Duplicate Admin",
                                  "email": "duplicate.admin@smartops.local",
                                  "function": "Platform Administrator",
                                  "roles": ["ADMIN"]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    private String loginAndExtractToken(String username, String password) throws Exception {
        String responseBody = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(responseBody, "$.accessToken");
    }
}