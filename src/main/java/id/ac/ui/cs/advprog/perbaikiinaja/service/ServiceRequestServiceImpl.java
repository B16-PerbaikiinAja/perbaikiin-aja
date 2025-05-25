package id.ac.ui.cs.advprog.perbaikiinaja.service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;

import id.ac.ui.cs.advprog.perbaikiinaja.dtos.CustomerServiceRequestDto;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ReportRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.service.wallet.WalletService;
import id.ac.ui.cs.advprog.perbaikiinaja.state.EstimatedState;
import id.ac.ui.cs.advprog.perbaikiinaja.state.PendingState;
import id.ac.ui.cs.advprog.perbaikiinaja.state.RejectedState;
import id.ac.ui.cs.advprog.perbaikiinaja.utils.PriceCalculationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Item;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ServiceRequestRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.service.coupon.CouponService;
import id.ac.ui.cs.advprog.perbaikiinaja.service.payment.PaymentMethodService;
import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;

/**
 * Implementation of the ServiceRequestService interface.
 * Provides business logic for managing service requests.
 */
@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;
    private final PaymentMethodService paymentMethodService;
    private final WalletService walletService;
    private final ReportRepository reportRepository;

    private static final String NOTASSIGNEDSTR = "This technician is not assigned to this service request";

    @Autowired
    public ServiceRequestServiceImpl(
            ServiceRequestRepository serviceRequestRepository,
            UserRepository userRepository,
            CouponService couponService,
            PaymentMethodService paymentMethodService,
            WalletService walletService,
            ReportRepository reportRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
        this.couponService = couponService;
        this.paymentMethodService = paymentMethodService;
        this.walletService = walletService;
        this.reportRepository = reportRepository;
    }

    @Override
    public List<ServiceRequest> findByTechnician(UUID technicianId) {
        return serviceRequestRepository.findByTechnicianId(technicianId);
    }

    @Override
    public List<ServiceRequest> findByTechnicianAndStatus(UUID technicianId, ServiceRequestStateType status) {
        List<ServiceRequest> requests = findByTechnician(technicianId);
        return requests.stream()
                .filter(request -> request.getStateType().equals(status))
                .toList();
    }

    @Override
    public List<ServiceRequest> findByCustomer(UUID customerId) {
        return serviceRequestRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<ServiceRequest> findById(UUID requestId) {
        return serviceRequestRepository.findById(requestId);
    }

    @Override
    public ServiceRequest provideEstimate(UUID requestId, RepairEstimate estimate, UUID technicianId) {
        ServiceRequest request = getServiceRequest(requestId);
        Technician technician = getTechnician(technicianId);

        // Ensure the technician is assigned to the request
        if (request.getTechnician() == null) {
            request.setTechnician(technician);
        } else if (!request.getTechnician().getId().equals(technicianId)) {
            throw new IllegalArgumentException(NOTASSIGNEDSTR);
        }

        // Pre-check the estimate values and fix if needed
        if (estimate.getCost() <= 0) {
            estimate.setCost(0.01); // Set a minimal positive value
        }
        
        if (estimate.getCompletionDate() == null) {
            estimate.setCompletionDate(LocalDate.now().plusDays(1)); // Set to tomorrow by default
        }

        
        request.provideEstimate(estimate);
        return serviceRequestRepository.save(request);
        
    }

    @Override
    public ServiceRequest acceptEstimate(UUID requestId, UUID customerId) {
        ServiceRequest request = getServiceRequest(requestId);

        // First check if the state transition is valid
        if (!(request.getState() instanceof EstimatedState)) {
            throw new IllegalStateException("Cannot accept estimate in current state");
        }

        // Then check if the customer owns the request
        if (!request.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("This customer does not own this service request");
        }

        // Accept the estimate
        request.acceptEstimate();

        return serviceRequestRepository.save(request);
    }

    @Override
    public ServiceRequest rejectEstimate(UUID requestId, UUID customerId) {
        ServiceRequest request = getServiceRequest(requestId);

        // Ensure the customer owns the request
        if (!request.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("This customer does not own this service request");
        }

        // Reject the estimate
        request.rejectEstimate();

        return serviceRequestRepository.save(request);
    }

    @Override
    public ServiceRequest startService(UUID requestId, UUID technicianId) {
        ServiceRequest request = getServiceRequest(requestId);

        // Ensure the technician is assigned to the request
        if (!request.getTechnician().getId().equals(technicianId)) {
            throw new IllegalArgumentException(NOTASSIGNEDSTR);
        }

        // Start the service
        request.startService();

        return serviceRequestRepository.save(request);
    }

    @Override
    public ServiceRequest completeService(UUID requestId, UUID technicianId) {
        ServiceRequest request = getServiceRequest(requestId);
        Technician technician = getTechnician(technicianId);

        // Ensure the technician is assigned to the request
        if (!request.getTechnician().getId().equals(technicianId)) {
            throw new IllegalArgumentException(NOTASSIGNEDSTR);
        }

        // Complete the service
        request.completeService();

        // Process payment - only if there's a valid estimate amount
        if (request.getEstimate() != null && request.getEstimate().getCost() > 0) {
            Customer customer = request.getCustomer();
            BigDecimal originalAmount = BigDecimal.valueOf(request.getEstimate().getCost());
            BigDecimal finalAmount = PriceCalculationUtils.calculateFinalPrice(originalAmount, request.getCoupon());

            // Process the payment between customer and technician wallets
            walletService.processServicePayment(
                    customer.getId(),
                    technician.getId(),
                    finalAmount,
                    request.getId()
            );
        }

        return serviceRequestRepository.save(request);
    }

    @Override
    public ServiceRequest createReport(UUID requestId, Report report, UUID technicianId) {
        ServiceRequest request = getServiceRequest(requestId);

        // Ensure the technician is assigned to the request
        if (!request.getTechnician().getId().equals(technicianId)) {
            throw new IllegalArgumentException(NOTASSIGNEDSTR);
        }

        Report savedReport = reportRepository.save(report);

        // Create the report
        request.createReport(savedReport);

        return serviceRequestRepository.save(request);
    }


    private Coupon getCouponByCode(String couponCode){
        if (couponCode != null && !couponCode.isEmpty()) {
            return couponService.getCouponByCode(couponCode)
                .orElse(null); 
        }
        return null;
    }

    private PaymentMethod getPaymentMethodById(UUID paymentMethodId) {
        if (paymentMethodId != null) {
            return paymentMethodService.findById(paymentMethodId)
                    .orElseThrow(() -> new IllegalArgumentException("Payment method not found with ID: " + paymentMethodId));
        } else {
            throw new IllegalArgumentException("Payment method ID is required");
        }
    }

    @Override
    public ServiceRequest createFromDto(CustomerServiceRequestDto dto, User user) {
        ServiceRequest request = new ServiceRequest();
        Item item = Item.builder()
                .name(dto.getName())
                .condition(dto.getCondition())
                .issueDescription(dto.getIssueDescription())
                .build();
        request.setItem(item);
        request.setServiceDate(dto.getServiceDate());
        request.setProblemDescription(dto.getIssueDescription());
        request.setCustomer((Customer) user);

        Technician randomTechnician = getRandomTechnician();
        request.setTechnician(randomTechnician);

        Coupon couponUsed = getCouponByCode(dto.getCouponCode());
        request.setCoupon(couponUsed);

        PaymentMethod paymentMethod = getPaymentMethodById(dto.getPaymentMethodId());
        request.setPaymentMethod(paymentMethod);

        return serviceRequestRepository.save(request);
    }

    private Technician getRandomTechnician() {
        Iterable<User> users = userRepository.findAll();
        List<Technician> allTechnicians = new java.util.ArrayList<>();
        for (User u : users) {
            if (u instanceof Technician technician) {
                allTechnicians.add(technician);
            }
        }
        if (allTechnicians.isEmpty()) {
            throw new IllegalStateException("No technician available to assign to this service request");
        }
        SecureRandom secureRandom = new SecureRandom();
        int randomIdx = secureRandom.nextInt(allTechnicians.size());
        return allTechnicians.get(randomIdx);
    }

    @Override
    public ServiceRequest updateFromDto(UUID requestId, CustomerServiceRequestDto dto, User user) {
        ServiceRequest existing = getServiceRequest(requestId);

        checkCanUpdate(existing, user);

        Item item = Item.builder()
                .name(dto.getName())
                .condition(dto.getCondition())
                .issueDescription(dto.getIssueDescription())
                .build();
        existing.setItem(item);
        existing.setServiceDate(dto.getServiceDate());
        existing.setProblemDescription(dto.getIssueDescription());
        
        Coupon couponUsed = getCouponByCode(dto.getCouponCode());
        existing.setCoupon(couponUsed);
        
        PaymentMethod paymentMethod = getPaymentMethodById(dto.getPaymentMethodId());
        existing.setPaymentMethod(paymentMethod);

        // Set state to Pending after update
        existing.setState(new PendingState());

        return serviceRequestRepository.save(existing);
    }

    @Override
    public void delete(UUID requestId, User user) {
        ServiceRequest existing = getServiceRequest(requestId);
        checkCanDelete(existing, user);

        serviceRequestRepository.deleteById(requestId);
    }

    private void checkCanUpdate(ServiceRequest existing, User user) {
        checkCustomerOwnership(existing, user);
        checkPendingOrRejectedState(existing);
    }

    private void checkCanDelete(ServiceRequest existing, User user) {
        checkCustomerOwnership(existing, user);
        checkPendingOrRejectedState(existing);
    }

    private void checkCustomerOwnership(ServiceRequest request, User user) {
        if (!(user instanceof Customer) || !request.getCustomer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only the owning customer can update this service request");
        }
    }

    private void checkPendingOrRejectedState(ServiceRequest request) {
        if ( !(request.getState() instanceof PendingState) && !(request.getState() instanceof RejectedState)) {
            throw new IllegalStateException("Only permitted in Pending or Rejected state");
        }
    }

    /**
     * Helper method to get a service request by ID or throw an exception.
     */
    private ServiceRequest getServiceRequest(UUID requestId) {
        return serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Service request not found with ID: " + requestId));
    }

    /**
     * Helper method to get a customer by ID or throw an exception.
     */
    private Customer getCustomer(UUID customerId) {
        var user = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + customerId));

        if (!(user instanceof Customer)) {
            throw new IllegalArgumentException("User with ID: " + customerId + " is not a Customer");
        }

        return (Customer) user;
    }

    /**
     * Helper method to get a technician by ID or throw an exception.
     */
    private Technician getTechnician(UUID technicianId) {
        var user = userRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + technicianId));

        if (!(user instanceof Technician)) {
            throw new IllegalArgumentException("User with ID: " + technicianId + " is not a Technician");
        }

        return (Technician) user;
    }
}