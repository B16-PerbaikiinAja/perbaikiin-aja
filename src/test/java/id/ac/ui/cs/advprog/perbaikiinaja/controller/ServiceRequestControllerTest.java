package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ServiceRequestControllerTest {

    @Mock
    private ServiceRequestService serviceRequestService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ServiceRequestController controller;

    private UUID technicianId;
    private UUID customerId;
    private UUID serviceRequestId;
    private Technician technician;
    private Customer customer;
    private ServiceRequest serviceRequest;
    private List<ServiceRequest> serviceRequests;

    @BeforeEach
    void setUp() {
        // Initialize test data
        technicianId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        serviceRequestId = UUID.randomUUID();

        // Mock technician
        technician = mock(Technician.class);
        lenient().when(technician.getId()).thenReturn(technicianId);
        lenient().when(technician.getRole()).thenReturn("TECHNICIAN");
        lenient().when(technician.getCompletedJobCount()).thenReturn(5);
        lenient().when(technician.getTotalEarnings()).thenReturn(1000.0);

        // Mock customer
        customer = mock(Customer.class);
        lenient().when(customer.getId()).thenReturn(customerId);
        lenient().when(customer.getRole()).thenReturn("CUSTOMER");

        // Mock service request
        serviceRequest = mock(ServiceRequest.class);
        lenient().when(serviceRequest.getId()).thenReturn(serviceRequestId);
        lenient().when(serviceRequest.getTechnician()).thenReturn(technician);
        lenient().when(serviceRequest.getCustomer()).thenReturn(customer);
        lenient().when(serviceRequest.getStateType()).thenReturn(ServiceRequestStateType.PENDING);

        // Create list of service requests
        serviceRequests = Arrays.asList(serviceRequest);
    }

    @Test
    void getTechnicianServiceRequests_Success() {
        // Arrange
        lenient().when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.findByTechnician(technicianId)).thenReturn(serviceRequests);

        // Act
        ResponseEntity<?> response = controller.getTechnicianServiceRequests(technicianId, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("status"));
        assertEquals("SUCCESS", responseBody.get("message"));

        @SuppressWarnings("unchecked")
        List<ServiceRequest> returnedRequests = (List<ServiceRequest>) responseBody.get("serviceRequests");
        assertEquals(1, returnedRequests.size());

        verify(serviceRequestService).findByTechnician(technicianId);
    }

    @Test
    void getTechnicianServiceRequests_WithStatusFilter_Success() {
        // Arrange
        ServiceRequestStateType status = ServiceRequestStateType.PENDING;
        lenient().when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.findByTechnicianAndStatus(technicianId, status)).thenReturn(serviceRequests);

        // Act
        ResponseEntity<?> response = controller.getTechnicianServiceRequests(technicianId, status);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("status"));

        verify(serviceRequestService).findByTechnicianAndStatus(technicianId, status);
    }

    @Test
    void getTechnicianServiceRequests_WithValidStatus_Success() {
        // Arrange
        ServiceRequestStateType validStatus = ServiceRequestStateType.PENDING;
        lenient().when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.findByTechnicianAndStatus(technicianId, validStatus))
                .thenReturn(serviceRequests);

        // Act
        ResponseEntity<?> response = controller.getTechnicianServiceRequests(technicianId, validStatus);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("status"));
        assertEquals("SUCCESS", responseBody.get("message"));

        // Verify the service WAS called with the correct parameters
        verify(serviceRequestService).findByTechnicianAndStatus(technicianId, validStatus);
    }

    @Test
    void getCustomerServiceRequests_Success() {
        // Arrange
        lenient().when(authentication.getPrincipal()).thenReturn(customer);
        when(serviceRequestService.findByCustomer(customerId)).thenReturn(serviceRequests);

        // Act
        ResponseEntity<?> response = controller.getCustomerServiceRequests(customerId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("status"));
        assertEquals("SUCCESS", responseBody.get("message"));

        verify(serviceRequestService).findByCustomer(customerId);
    }

    @Test
    void updateServiceRequestStatus_ToInProgress_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));
        when(serviceRequestService.startService(serviceRequestId, technicianId)).thenReturn(serviceRequest);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", ServiceRequestStateType.IN_PROGRESS);

        // Act
        ResponseEntity<?> response = controller.updateServiceRequestStatus(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(serviceRequestService).startService(serviceRequestId, technicianId);
    }

    @Test
    void updateServiceRequestStatus_ToCompleted_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));
        when(serviceRequestService.completeService(serviceRequestId, technicianId)).thenReturn(serviceRequest);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", ServiceRequestStateType.COMPLETED);
        requestBody.put("finalPrice", 500.0);

        // Act
        ResponseEntity<?> response = controller.updateServiceRequestStatus(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(serviceRequestService).completeService(serviceRequestId, technicianId);
    }

    @Test
    void updateServiceRequestStatus_ServiceRequestNotFound_ReturnsNotFound() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.empty());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", ServiceRequestStateType.IN_PROGRESS);

        // Act
        ResponseEntity<?> response = controller.updateServiceRequestStatus(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4040, responseBody.get("errorCode"));

        verify(serviceRequestService, never()).startService(any(), any());
        verify(serviceRequestService, never()).completeService(any(), any());
    }

    @Test
    void updateServiceRequestStatus_TechnicianNotAssigned_ReturnsForbidden() {
        // Arrange
        Technician differentTechnician = mock(Technician.class);
        UUID differentTechnicianId = UUID.randomUUID();
        when(differentTechnician.getId()).thenReturn(differentTechnicianId);
        lenient().when(differentTechnician.getRole()).thenReturn("TECHNICIAN");

        when(authentication.getPrincipal()).thenReturn(differentTechnician);
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", ServiceRequestStateType.IN_PROGRESS);

        // Act
        ResponseEntity<?> response = controller.updateServiceRequestStatus(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4030, responseBody.get("errorCode"));

        verify(serviceRequestService, never()).startService(any(), any());
    }

    @Test
    void updateServiceRequestStatus_InvalidStatus_ReturnsBadRequest() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", "INVALID_STATUS");

        // Act
        ResponseEntity<?> response = controller.updateServiceRequestStatus(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4000, responseBody.get("errorCode"));

        verify(serviceRequestService, never()).startService(any(), any());
    }

    @Test
    void updateServiceRequestStatus_CompletedWithoutFinalPrice_ReturnsBadRequest() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", ServiceRequestStateType.COMPLETED);
        // Missing finalPrice

        // Act
        ResponseEntity<?> response = controller.updateServiceRequestStatus(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4001, responseBody.get("errorCode"));

        verify(serviceRequestService, never()).completeService(any(), any());
    }

    @Test
    void updateServiceRequestStatus_IllegalStateException_ReturnsBadRequest() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));
        when(serviceRequestService.startService(serviceRequestId, technicianId))
                .thenThrow(new IllegalStateException("Cannot transition to IN_PROGRESS from current state"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", ServiceRequestStateType.IN_PROGRESS);

        // Act
        ResponseEntity<?> response = controller.updateServiceRequestStatus(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4002, responseBody.get("errorCode"));
    }

    @Test
    void getAllServiceRequests_Success() {
        // We would need to implement the findAll method in the service
        // For now, just test that the endpoint returns a success response

        // Arrange
        User admin = mock(User.class);
        lenient().when(admin.getRole()).thenReturn("ADMIN");
        lenient().when(authentication.getPrincipal()).thenReturn(admin);

        // Act
        ResponseEntity<?> response = controller.getAllServiceRequests();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("status"));
        assertEquals("SUCCESS", responseBody.get("message"));
    }
}