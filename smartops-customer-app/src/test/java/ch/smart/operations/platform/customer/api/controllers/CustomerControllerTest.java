package ch.smart.operations.platform.customer.api.controllers;

import ch.smart.operations.platform.customer.application.dtos.CustomerDto;
import ch.smart.operations.platform.customer.application.services.CustomerApplicationService;
import ch.smart.operations.platform.customer.domain.enums.CustomerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerControllerTest {

    private MockMvc mockMvc;
    private CustomerApplicationService customerApplicationService;

    @BeforeEach
    void setUp() {
        customerApplicationService = mock(CustomerApplicationService.class);

        CustomerController controller = new CustomerController(customerApplicationService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void getAllCustomer_shouldReturnCustomers() throws Exception {
        UUID customerId = UUID.randomUUID();

        CustomerDto customer = new CustomerDto(
                customerId,
                "CUST-001",
                "Clinic Edelweiss SA",
                "Clinic Edelweiss",
                CustomerStatus.ACTIVE,
                "CHE-123456789",
                "Healthcare",
                "Important customer"
        );

        when(customerApplicationService.getAllCustomers())
                .thenReturn(List.of(customer));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(customerId.toString()))
                .andExpect(jsonPath("$[0].customerNumber").value("CUST-001"))
                .andExpect(jsonPath("$[0].legalName").value("Clinic Edelweiss SA"))
                .andExpect(jsonPath("$[0].displayName").value("Clinic Edelweiss"))
                .andExpect(jsonPath("$[0].status").value("Active"))
                .andExpect(jsonPath("$[0].taxIdentifier").value("CHE-123456789"))
                .andExpect(jsonPath("$[0].industry").value("Healthcare"))
                .andExpect(jsonPath("$[0].notes").value("Important customer"));
    }

    @Test
    void getCustomerById_shouldReturnCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();

        CustomerDto customer = new CustomerDto(
                customerId,
                "CUST-001",
                "Clinic Edelweiss SA",
                "Clinic Edelweiss",
                CustomerStatus.ACTIVE,
                "CHE-123456789",
                "Healthcare",
                "Important customer"
        );

        when(customerApplicationService.getCustomerById(customerId))
                .thenReturn(customer);

        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.legalName").value("Clinic Edelweiss SA"))
                .andExpect(jsonPath("$.displayName").value("Clinic Edelweiss"));
    }

    @Test
    void createCustomer_shouldReturnCreatedId() throws Exception {
        UUID customerId = UUID.randomUUID();

        when(customerApplicationService.createCustomer(any()))
                .thenReturn(customerId);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "legalName": "Clinic Edelweiss SA",
                                  "displayName": "Clinic Edelweiss",
                                  "taxIdentifier": "CHE-123456789",
                                  "industry": "Healthcare",
                                  "notes": "Important customer"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/customer/" + customerId))
                .andExpect(jsonPath("$.id").value(customerId.toString()));

        verify(customerApplicationService).createCustomer(any());
    }
}