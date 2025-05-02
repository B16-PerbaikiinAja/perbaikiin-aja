package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(TechnicianServiceRequestController.class)
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
}
