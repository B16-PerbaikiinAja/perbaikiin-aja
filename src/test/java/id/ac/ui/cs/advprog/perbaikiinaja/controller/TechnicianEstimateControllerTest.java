package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
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

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(TechnicianEstimateController.class)
public class TechnicianEstimateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceRequestService serviceRequestService;

    private Technician technician;
    private UUID technicianId;
    private UUID serviceRequestId;
    private ServiceRequest serviceRequest;
    private RepairEstimate estimate;

    @BeforeEach
    void setUp() {
        technicianId = UUID.randomUUID();
        serviceRequestId = UUID.randomUUID();

        technician = mock(Technician.class);
        when(technician.getId()).thenReturn(technicianId);
        when(technician.getRole()).thenReturn("TECHNICIAN");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(technician, null)
        );

        serviceRequest = mock(ServiceRequest.class);
        when(serviceRequest.getId()).thenReturn(serviceRequestId);

        estimate = new RepairEstimate();
        estimate.setCost(250000);
        estimate.setCompletionDate(LocalDate.now().plusDays(7));
        estimate.setNotes("Needs new parts");

        when(serviceRequestService.provideEstimate(eq(serviceRequestId), any(RepairEstimate.class), eq(technicianId)))
                .thenReturn(serviceRequest);
    }
}
