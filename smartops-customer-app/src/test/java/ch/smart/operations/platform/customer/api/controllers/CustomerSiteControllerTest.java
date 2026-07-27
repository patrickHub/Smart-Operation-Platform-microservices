package ch.smart.operations.platform.customer.api.controllers;

import ch.smart.operations.platform.customer.application.dtos.CustomerSiteDto;
import ch.smart.operations.platform.customer.application.services.CustomerSiteApplicationService;
import ch.smart.operations.platform.customer.domain.enums.CustomerSiteStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerSiteControllerTest {

    private MockMvc mockMvc;
    private CustomerSiteApplicationService customerSiteApplicationService;

    @BeforeEach
    void setUp() {
        customerSiteApplicationService = mock(CustomerSiteApplicationService.class);

        CustomerSiteController controller = new CustomerSiteController(customerSiteApplicationService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void getAllCustomerSites_shouldReturnSites() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID siteId = UUID.randomUUID();

        CustomerSiteDto site = siteDto(siteId, customerId, "Main Hospital");

        when(customerSiteApplicationService.getAllCustomerSites())
                .thenReturn(List.of(site));

        mockMvc.perform(get("/api/v1/sites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(siteId.toString()))
                .andExpect(jsonPath("$[0].customerId").value(customerId.toString()))
                .andExpect(jsonPath("$[0].siteName").value("Main Hospital"))
                .andExpect(jsonPath("$[0].status").value("Active"));
    }

    @Test
    void getCustomerSitesByCustomerId_shouldReturnSites() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID siteId = UUID.randomUUID();

        CustomerSiteDto site = siteDto(siteId, customerId, "Main Hospital");

        when(customerSiteApplicationService.getCustomerSitesByCustomerId(customerId))
                .thenReturn(List.of(site));

        mockMvc.perform(get("/api/v1/customers/{customerId}/sites", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(siteId.toString()))
                .andExpect(jsonPath("$[0].customerId").value(customerId.toString()))
                .andExpect(jsonPath("$[0].siteName").value("Main Hospital"));
    }

    @Test
    void getCustomerSiteById_shouldReturnSite() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID siteId = UUID.randomUUID();

        CustomerSiteDto site = siteDto(siteId, customerId, "Main Hospital");

        when(customerSiteApplicationService.getCustomerSiteById(siteId))
                .thenReturn(site);

        mockMvc.perform(get("/api/v1/sites/{id}", siteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(siteId.toString()))
                .andExpect(jsonPath("$.siteName").value("Main Hospital"))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()));
    }

    @Test
    void createCustomerSite_shouldReturnCreatedId() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID siteId = UUID.randomUUID();

        when(customerSiteApplicationService.createCustomerSite(any()))
                .thenReturn(siteId);

        mockMvc.perform(post("/api/v1/customers/{customerId}/sites", customerId)
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
                .andExpect(header().string("Location", "/api/v1/sites/" + siteId))
                .andExpect(jsonPath("$.id").value(siteId.toString()));

        verify(customerSiteApplicationService).createCustomerSite(any());
    }

    private CustomerSiteDto siteDto(UUID siteId, UUID customerId, String siteName) {
        return new CustomerSiteDto(
                siteId,
                "SITE-001",
                customerId,
                siteName,
                CustomerSiteStatus.ACTIVE,
                "Rue de Lausanne 10",
                "Building A",
                "Fribourg",
                "FR",
                "1700",
                "CH",
                "Europe/Zurich",
                "Use main entrance",
                0L,
                "2026-01-01T10:00:00Z"
        );
    }
}