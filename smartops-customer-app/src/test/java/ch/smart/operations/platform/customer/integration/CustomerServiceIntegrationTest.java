package ch.smart.operations.platform.customer.integration;

import com.jayway.jsonpath.JsonPath;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
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

                "spring.jpa.hibernate.ddl-auto=validate",
                "spring.flyway.enabled=true",
                "spring.flyway.locations=classpath:db/migration",
                "spring.flyway.schemas=customer",
                "spring.flyway.default-schema=customer",
                "spring.flyway.create-schemas=true",
                "spring.flyway.table=flyway_schema_history",
                "spring.jpa.properties.hibernate.default_schema=customer"
        }
)
@AutoConfigureMockMvc
class CustomerServiceIntegrationTest {

    private static final String ISSUER = "smartops-identity";
    private static final String SECRET = "local-dev-secret-change-me-local-dev-secret-change-me";

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("smartops_customer_test")
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
    void getCustomers_shouldReturnUnauthorized_whenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCustomer_shouldReturnForbidden_whenTechnicianTokenIsProvided() throws Exception {
        String technicianToken = tokenWithRoles("technician", List.of("TECHNICIAN"));

        mockMvc.perform(post("/api/v1/customers")
                        .header("Authorization", "Bearer " + technicianToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "legalName": "Forbidden Clinic SA",
                                  "displayName": "Forbidden Clinic",
                                  "taxIdentifier": "CHE-999999999",
                                  "industry": "Healthcare",
                                  "notes": "Should not be created by technician"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCustomer_shouldCreateCustomer_whenAdminTokenIsProvided() throws Exception {
        String adminToken = tokenWithRoles("admin", List.of("ADMIN"));

        String responseBody = mockMvc.perform(post("/api/v1/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "legalName": "Clinic Edelweiss SA",
                                  "displayName": "Clinic Edelweiss",
                                  "taxIdentifier": "CHE-123456789",
                                  "industry": "Healthcare",
                                  "notes": "Important hospital customer"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String customerId = JsonPath.read(responseBody, "$.id");

        mockMvc.perform(get("/api/v1/customers/{id}", customerId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.legalName").value("Clinic Edelweiss SA"))
                .andExpect(jsonPath("$.displayName").value("Clinic Edelweiss"))
                .andExpect(jsonPath("$.status").value("Active"))
                .andExpect(jsonPath("$.taxIdentifier").value("CHE-123456789"))
                .andExpect(jsonPath("$.industry").value("Healthcare"))
                .andExpect(jsonPath("$.notes").value("Important hospital customer"));
    }

    @Test
    void createCustomer_shouldReturnBadRequest_whenDisplayNameEqualsLegalName() throws Exception {
        String adminToken = tokenWithRoles("admin", List.of("ADMIN"));

        mockMvc.perform(post("/api/v1/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "legalName": "Same Name Clinic SA",
                                  "displayName": "Same Name Clinic SA",
                                  "taxIdentifier": "CHE-111111111",
                                  "industry": "Healthcare",
                                  "notes": "Invalid customer"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCustomers_shouldReturnCustomers_whenDispatcherTokenIsProvided() throws Exception {
        String adminToken = tokenWithRoles("admin", List.of("ADMIN"));
        String dispatcherToken = tokenWithRoles("dispatcher", List.of("DISPATCHER"));

        createCustomer(adminToken, "Dispatcher Read Clinic SA", "Dispatcher Read Clinic");

        mockMvc.perform(get("/api/v1/customers")
                        .header("Authorization", "Bearer " + dispatcherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createCustomerSite_shouldCreateSiteForExistingCustomer_whenAdminTokenIsProvided() throws Exception {
        String adminToken = tokenWithRoles("admin", List.of("ADMIN"));

        String customerId = createCustomer(
                adminToken,
                "Site Integration Clinic SA",
                "Site Integration Clinic"
        );

        String responseBody = mockMvc.perform(post("/api/v1/customers/{customerId}/sites", customerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "siteName": "Main Hospital",
                                  "addressLine1": "Rue de Lausanne 10",
                                  "addressLine2": "Building A",
                                  "city": "Fribourg",
                                  "stateRegion": "FR",
                                  "postalCode": "1700",
                                  "countryCode": "CH",
                                  "timezone": "Europe/Zurich",
                                  "accessInstructions": "Use main entrance"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String siteId = JsonPath.read(responseBody, "$.id");

        mockMvc.perform(get("/api/v1/sites/{id}", siteId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(siteId))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.siteName").value("Main Hospital"))
                .andExpect(jsonPath("$.city").value("Fribourg"))
                .andExpect(jsonPath("$.countryCode").value("CH"));
    }

    @Test
    void createCustomerSite_shouldReturnBadRequest_whenCustomerDoesNotExist() throws Exception {
        String adminToken = tokenWithRoles("admin", List.of("ADMIN"));
        UUID missingCustomerId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/customers/{customerId}/sites", missingCustomerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "siteName": "Unknown Customer Site",
                                  "addressLine1": "Rue de Lausanne 10",
                                  "addressLine2": "Building A",
                                  "city": "Fribourg",
                                  "stateRegion": "FR",
                                  "postalCode": "1700",
                                  "countryCode": "CH",
                                  "timezone": "Europe/Zurich",
                                  "accessInstructions": "Use main entrance"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCustomerContact_shouldCreateContactForExistingCustomer_whenAdminTokenIsProvided() throws Exception {
        String adminToken = tokenWithRoles("admin", List.of("ADMIN"));

        String customerId = createCustomer(
                adminToken,
                "Contact Integration Clinic SA",
                "Contact Integration Clinic"
        );

        String responseBody = mockMvc.perform(post("/api/v1/customers/{customerId}/contacts", customerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doctor",
                                  "email": "john.doctor.integration@clinic.local",
                                  "phone": "+41 26 000 00 00",
                                  "contactRole": "Medical Director",
                                  "isPrimary": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String contactId = JsonPath.read(responseBody, "$.id");

        mockMvc.perform(get("/api/v1/contacts/{contactId}", contactId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contactId))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doctor"))
                .andExpect(jsonPath("$.email").value("john.doctor.integration@clinic.local"))
                .andExpect(jsonPath("$.isPrimary").value(true));
    }

    @Test
    void createCustomerContact_shouldReturnBadRequest_whenPrimaryContactAlreadyExists() throws Exception {
        String adminToken = tokenWithRoles("admin", List.of("ADMIN"));

        String customerId = createCustomer(
                adminToken,
                "Primary Contact Clinic SA",
                "Primary Contact Clinic"
        );

        createContact(adminToken, customerId, "primary.one@clinic.local", true);

        mockMvc.perform(post("/api/v1/customers/{customerId}/contacts", customerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Second",
                                  "lastName": "Primary",
                                  "email": "primary.two@clinic.local",
                                  "phone": "+41 26 000 00 01",
                                  "contactRole": "Medical Director",
                                  "isPrimary": true
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void internalCustomerExists_shouldReturnNoContent_whenCustomerExists() throws Exception {
        String adminToken = tokenWithRoles("admin", List.of("ADMIN"));

        String customerId = createCustomer(
                adminToken,
                "Internal Check Clinic SA",
                "Internal Check Clinic"
        );

        mockMvc.perform(get("/internal/v1/customers/{customerId}/exists", customerId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void internalCustomerSiteExists_shouldReturnNoContent_whenCustomerAndSiteExist() throws Exception {
        String adminToken = tokenWithRoles("admin", List.of("ADMIN"));

        String customerId = createCustomer(
                adminToken,
                "Internal Site Check Clinic SA",
                "Internal Site Check Clinic"
        );

        String siteId = createSite(adminToken, customerId, "Internal Main Site");

        mockMvc.perform(get("/internal/v1/customers/{customerId}/sites/{siteId}/exists", customerId, siteId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    private String createCustomer(String token, String legalName, String displayName) throws Exception {
        String responseBody = mockMvc.perform(post("/api/v1/customers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "legalName": "%s",
                                  "displayName": "%s",
                                  "taxIdentifier": "CHE-%d",
                                  "industry": "Healthcare",
                                  "notes": "Integration test customer"
                                }
                                """.formatted(legalName, displayName, Math.abs(legalName.hashCode()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(responseBody, "$.id");
    }

    private String createSite(String token, String customerId, String siteName) throws Exception {
        String responseBody = mockMvc.perform(post("/api/v1/customers/{customerId}/sites", customerId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "siteName": "%s",
                                  "addressLine1": "Rue de Lausanne 10",
                                  "addressLine2": "Building A",
                                  "city": "Fribourg",
                                  "stateRegion": "FR",
                                  "postalCode": "1700",
                                  "countryCode": "CH",
                                  "timezone": "Europe/Zurich",
                                  "accessInstructions": "Use main entrance"
                                }
                                """.formatted(siteName)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(responseBody, "$.id");
    }

    private String createContact(String token, String customerId, String email, boolean isPrimary) throws Exception {
        String responseBody = mockMvc.perform(post("/api/v1/customers/{customerId}/contacts", customerId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doctor",
                                  "email": "%s",
                                  "phone": "+41 26 000 00 00",
                                  "contactRole": "Medical Director",
                                  "isPrimary": %s
                                }
                                """.formatted(email, isPrimary)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(responseBody, "$.id");
    }

    private String tokenWithRoles(String username, List<String> roles) {
        Instant now = Instant.now();

        SecretKey key = new SecretKeySpec(
                SECRET.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        JwtEncoder jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(UUID.randomUUID().toString())
                .claim("preferred_username", username)
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}