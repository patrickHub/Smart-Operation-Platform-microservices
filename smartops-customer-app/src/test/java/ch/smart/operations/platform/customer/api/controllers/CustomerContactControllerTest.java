package ch.smart.operations.platform.customer.api.controllers;

import ch.smart.operations.platform.customer.application.dtos.CustomerContactDto;
import ch.smart.operations.platform.customer.application.services.CustomerContactApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerContactControllerTest {

    private MockMvc mockMvc;
    private CustomerContactApplicationService customerContactApplicationService;

    @BeforeEach
    void setUp() {
        customerContactApplicationService = mock(CustomerContactApplicationService.class);

        CustomerContactController controller = new CustomerContactController(customerContactApplicationService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void createCustomerContact_shouldReturnCreatedId() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID contactId = UUID.randomUUID();

        when(customerContactApplicationService.createCustomerContact(any()))
                .thenReturn(contactId);

        mockMvc.perform(post("/api/v1/customers/{customerId}/contacts", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doctor",
                                  "email": "john.doctor@clinic.local",
                                  "phone": "+41 26 000 00 00",
                                  "contactRole": "Medical Director",
                                  "isPrimary": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/contacts/" + contactId))
                .andExpect(jsonPath("$.id").value(contactId.toString()));

        verify(customerContactApplicationService).createCustomerContact(any());
    }

    @Test
    void getContactById_shouldReturnContact() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID contactId = UUID.randomUUID();

        CustomerContactDto contact = contactDto(contactId, customerId);

        when(customerContactApplicationService.getContactById(contactId))
                .thenReturn(contact);

        mockMvc.perform(get("/api/v1/contacts/{contactId}", contactId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contactId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doctor"))
                .andExpect(jsonPath("$.email").value("john.doctor@clinic.local"))
                .andExpect(jsonPath("$.isPrimary").value(true));
    }

    @Test
    void getContactsByCustomerId_shouldReturnContacts() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID contactId = UUID.randomUUID();

        CustomerContactDto contact = contactDto(contactId, customerId);

        when(customerContactApplicationService.getContactsByCustomerId(customerId))
                .thenReturn(List.of(contact));

        mockMvc.perform(get("/api/v1/customers/{customerId}/contacts", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(contactId.toString()))
                .andExpect(jsonPath("$[0].customerId").value(customerId.toString()))
                .andExpect(jsonPath("$[0].email").value("john.doctor@clinic.local"));
    }

    private CustomerContactDto contactDto(UUID contactId, UUID customerId) {
        OffsetDateTime now = OffsetDateTime.now();

        return new CustomerContactDto(
                contactId,
                customerId,
                "John",
                "Doctor",
                "john.doctor@clinic.local",
                "+41 26 000 00 00",
                "Medical Director",
                true,
                "Active",
                now,
                now
        );
    }
}
