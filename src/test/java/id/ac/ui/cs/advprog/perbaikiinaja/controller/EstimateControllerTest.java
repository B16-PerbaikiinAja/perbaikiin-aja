package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.service.EstimateService;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import id.ac.ui.cs.advprog.perbaikiinaja.service.wallet.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EstimateControllerTest {

    @Mock
    private ServiceRequestService serviceRequestService;

    @Mock
    private EstimateService estimateService;

    @Mock
    private Authentication authentication;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private EstimateController controller;

    private UUID technicianId;
    private UUID customerId;
    private UUID serviceRequestId;
    private Technician technician;
    private Customer customer;
    private ServiceRequest serviceRequest;
    private RepairEstimate estimate;

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

        // Mock customer
        customer = mock(Customer.class);
        lenient().when(customer.getId()).thenReturn(customerId);
        lenient().when(customer.getRole()).thenReturn("CUSTOMER");

        // Mock estimate
        estimate = mock(RepairEstimate.class);
        lenient().when(estimate.getCost()).thenReturn(100.0);
        lenient().when(estimate.getCompletionDate()).thenReturn(LocalDate.now().plusDays(3));
        lenient().when(estimate.getNotes()).thenReturn("Test notes");
        lenient().when(estimate.getCreatedDate()).thenReturn(LocalDate.now());

        // Mock service request
        serviceRequest = mock(ServiceRequest.class);
        lenient().when(serviceRequest.getId()).thenReturn(serviceRequestId);
        lenient().when(serviceRequest.getTechnician()).thenReturn(technician);
        lenient().when(serviceRequest.getCustomer()).thenReturn(customer);
        lenient().when(serviceRequest.getEstimate()).thenReturn(estimate);
        lenient().when(serviceRequest.getStateType()).thenReturn(ServiceRequestStateType.ESTIMATED);

        controller = new EstimateController(serviceRequestService, estimateService, walletService);
    }

    @Test
    void createEstimate_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.provideEstimate(eq(serviceRequestId), any(RepairEstimate.class), eq(technicianId)))
                .thenReturn(serviceRequest);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("estimatedCost", 100.0);
        requestBody.put("estimatedCompletionTime", LocalDate.now().plusDays(3).toString());
        requestBody.put("notes", "Test notes");

        // Act
        ResponseEntity<?> response = controller.createEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        Map<String, Object> estimateResponse = (Map<String, Object>) responseBody.get("estimate");
        assertNotNull(estimateResponse);
        assertEquals(serviceRequestId, estimateResponse.get("serviceRequestId"));
        assertEquals(100.0, estimateResponse.get("estimatedCost"));
        assertEquals(ServiceRequestStateType.PENDING, estimateResponse.get("status"));

        verify(serviceRequestService).provideEstimate(eq(serviceRequestId), any(RepairEstimate.class), eq(technicianId));
    }

    @Test
    void createEstimate_NegativeCost_ReturnsBadRequest() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("estimatedCost", -50.0);
        requestBody.put("estimatedCompletionTime", LocalDate.now().plusDays(3).toString());

        // Act
        ResponseEntity<?> response = controller.createEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4000, responseBody.get("errorCode"));

        verify(serviceRequestService, never()).provideEstimate(any(), any(), any());
    }

    @Test
    void createEstimate_PastCompletionDate_ReturnsBadRequest() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("estimatedCost", 100.0);
        requestBody.put("estimatedCompletionTime", LocalDate.now().minusDays(1).toString());

        // Act
        ResponseEntity<?> response = controller.createEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4001, responseBody.get("errorCode"));

        verify(serviceRequestService, never()).provideEstimate(any(), any(), any());
    }

    @Test
    void createEstimate_ServiceRequestNotFound_ReturnsNotFound() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.provideEstimate(eq(serviceRequestId), any(RepairEstimate.class), eq(technicianId)))
                .thenThrow(new IllegalArgumentException("Service request not found"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("estimatedCost", 100.0);
        requestBody.put("estimatedCompletionTime", LocalDate.now().plusDays(3).toString());

        // Act
        ResponseEntity<?> response = controller.createEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4040, responseBody.get("errorCode"));
    }

    @Test
    void createEstimate_InvalidState_ReturnsConflict() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.provideEstimate(eq(serviceRequestId), any(RepairEstimate.class), eq(technicianId)))
                .thenThrow(new IllegalStateException("Cannot provide estimate in current state"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("estimatedCost", 100.0);
        requestBody.put("estimatedCompletionTime", LocalDate.now().plusDays(3).toString());

        // Act
        ResponseEntity<?> response = controller.createEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4090, responseBody.get("errorCode"));
    }

    @Test
    void respondToEstimate_Accept_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(customer);
        when(estimateService.findById(serviceRequestId)).thenReturn(Optional.of(estimate));
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));
        when(estimateService.acceptEstimate(serviceRequestId, customerId, "Great estimate!")).thenReturn(serviceRequest);

        // Mock wallet check
        Wallet customerWallet = mock(Wallet.class);
        when(customerWallet.getBalance()).thenReturn(new BigDecimal("200.00"));
        when(walletService.getWalletByUserId(customerId)).thenReturn(Optional.of(customerWallet));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "ACCEPT");
        requestBody.put("feedback", "Great estimate!");

        // Act
        ResponseEntity<?> response = controller.respondToEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        Map<String, Object> estimateResponse = (Map<String, Object>) responseBody.get("estimate");
        assertNotNull(estimateResponse);
        assertEquals(ServiceRequestStateType.ACCEPTED, estimateResponse.get("status"));
        assertEquals("Great estimate!", estimateResponse.get("feedback"));

        verify(estimateService).acceptEstimate(serviceRequestId, customerId, "Great estimate!");
    }

    @Test
    void respondToEstimate_Accept_InsufficientFunds_ReturnsBadRequest() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(customer);
        when(estimateService.findById(serviceRequestId)).thenReturn(Optional.of(estimate));
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));

        // Mock wallet with insufficient funds
        Wallet customerWallet = mock(Wallet.class);
        when(customerWallet.getBalance()).thenReturn(new BigDecimal("50.00")); // Less than estimate cost
        when(walletService.getWalletByUserId(customerId)).thenReturn(Optional.of(customerWallet));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "ACCEPT");
        requestBody.put("feedback", "Great estimate!");

        // Act
        ResponseEntity<?> response = controller.respondToEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4002, responseBody.get("errorCode"));

        verify(estimateService, never()).acceptEstimate(any(), any(), any());
    }

    @Test
    void respondToEstimate_Reject_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(customer);
        when(estimateService.findById(serviceRequestId)).thenReturn(Optional.of(estimate));
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));
        when(estimateService.rejectEstimate(serviceRequestId, customerId, "Too expensive")).thenReturn(serviceRequestId);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "REJECT");
        requestBody.put("feedback", "Too expensive");

        // Act
        ResponseEntity<?> response = controller.respondToEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Estimate rejected and service request deleted successfully", responseBody.get("message"));
        assertEquals(serviceRequestId.toString(), responseBody.get("serviceRequestId"));

        verify(estimateService).rejectEstimate(serviceRequestId, customerId, "Too expensive");
    }

    @Test
    void respondToEstimate_InvalidAction_ReturnsBadRequest() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(customer);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "INVALID_ACTION");

        // Act
        ResponseEntity<?> response = controller.respondToEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4000, responseBody.get("errorCode"));

        verifyNoInteractions(estimateService);
    }

    @Test
    void respondToEstimate_EstimateNotFound_ReturnsNotFound() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(customer);
        when(estimateService.findById(serviceRequestId)).thenReturn(Optional.empty());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "ACCEPT");

        // Act
        ResponseEntity<?> response = controller.respondToEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4040, responseBody.get("errorCode"));

        verify(estimateService, never()).acceptEstimate(any(), any(), any());
        verify(estimateService, never()).rejectEstimate(any(), any(), any());
    }

    @Test
    void respondToEstimate_NotOwner_ReturnsForbidden() {
        // Arrange
        Customer differentCustomer = mock(Customer.class);
        UUID differentCustomerId = UUID.randomUUID();
        when(differentCustomer.getId()).thenReturn(differentCustomerId);
        lenient().when(differentCustomer.getRole()).thenReturn("CUSTOMER");

        when(authentication.getPrincipal()).thenReturn(differentCustomer);
        when(estimateService.findById(serviceRequestId)).thenReturn(Optional.of(estimate));
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "ACCEPT");

        // Act
        ResponseEntity<?> response = controller.respondToEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4030, responseBody.get("errorCode"));

        verify(estimateService, never()).acceptEstimate(any(), any(), any());
    }

    @Test
    void respondToEstimate_ServiceRequestNotFound_ReturnsNotFound() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(customer);
        when(estimateService.findById(serviceRequestId)).thenReturn(Optional.of(estimate));
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.empty());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "ACCEPT");

        // Act
        ResponseEntity<?> response = controller.respondToEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4041, responseBody.get("errorCode"));

        verify(estimateService, never()).acceptEstimate(any(), any(), any());
    }

    @Test
    void respondToEstimate_InvalidState_ReturnsConflict() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(customer);
        when(estimateService.findById(serviceRequestId)).thenReturn(Optional.of(estimate));
        when(serviceRequestService.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));
        when(estimateService.acceptEstimate(serviceRequestId, customerId, "")).thenThrow(new IllegalStateException("Estimate already accepted"));

        // Mock wallet with sufficient funds
        Wallet customerWallet = mock(Wallet.class);
        when(customerWallet.getBalance()).thenReturn(new BigDecimal("200.00"));
        when(walletService.getWalletByUserId(customerId)).thenReturn(Optional.of(customerWallet));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "ACCEPT");

        // Act
        ResponseEntity<?> response = controller.respondToEstimate(serviceRequestId, requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4090, responseBody.get("errorCode"));
    }
}