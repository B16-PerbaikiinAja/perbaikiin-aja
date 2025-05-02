package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TechnicianServiceRequestController.class)
@Import(TestSecurityConfig.class)
public class TechnicianServiceRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceRequestService serviceRequestService;

    private Technician technician;
    private UUID technicianId;
    private List<ServiceRequest> serviceRequests;

    @BeforeEach
    void setUp() {
        technicianId = UUID.randomUUID();

        technician = mock(Technician.class);
        when(technician.getId()).thenReturn(technicianId);
        when(technician.getRole()).thenReturn("TECHNICIAN");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(technician, null)
        );

        serviceRequests = Arrays.asList(
                mock(ServiceRequest.class),
                mock(ServiceRequest.class)
        );

        when(serviceRequestService.findByTechnician(technicianId)).thenReturn(serviceRequests);
    }

    @Test
    void getServiceRequests_ShouldReturnServiceRequests() throws Exception {
        mockMvc.perform(get("/technician/service-requests/{technicianId}", technicianId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.serviceRequests").isArray());

        verify(serviceRequestService).findByTechnician(technicianId);
    }

    @Test
    void getServiceRequests_WithStatusParam_ShouldFilterRequests() throws Exception {
        String status = "PENDING";
        List<ServiceRequest> filteredRequests = Arrays.asList(mock(ServiceRequest.class));

        when(serviceRequestService.findByTechnicianAndStatus(technicianId, status))
                .thenReturn(filteredRequests);

        mockMvc.perform(get("/technician/service-requests/{technicianId}", technicianId)
                        .param("status", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.serviceRequests").isArray());

        verify(serviceRequestService).findByTechnicianAndStatus(technicianId, status);
    }

    @Test
    void getServiceRequests_WithInvalidStatus_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/technician/service-requests/{technicianId}", technicianId)
                        .param("status", "INVALID_STATUS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("INVALID_STATUS_PARAMETER"));
    }

    @Test
    void getServiceRequests_WithDifferentTechnician_ShouldReturnForbidden() throws Exception {
        UUID differentTechnicianId = UUID.randomUUID();

        mockMvc.perform(get("/technician/service-requests/{technicianId}", differentTechnicianId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("ACCESS_DENIED"));
    }
}
