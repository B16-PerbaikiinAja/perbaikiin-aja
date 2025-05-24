package id.ac.ui.cs.advprog.perbaikiinaja.model;

import java.time.LocalDate;
import java.util.UUID;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import id.ac.ui.cs.advprog.perbaikiinaja.state.PendingState;
import id.ac.ui.cs.advprog.perbaikiinaja.state.ServiceRequestState;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;

/**
 * Represents a service request in the repair system.
 * Contains information about the item to be repaired,
 * the customer, technician, and the current state of the request.
 */

@Entity
@Table(name = "service_requests")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Technician technician;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    private Item item;

    private LocalDate requestDate;
    private LocalDate serviceDate;
    private String problemDescription;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "estimate_id")
    private RepairEstimate estimate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "report_id")
    private Report report;

    
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    @Getter
    @Setter
    private Coupon coupon;

    @Transient
    private ServiceRequestState state;

    public ServiceRequest() {
        this.requestDate = LocalDate.now();
        this.state = new PendingState();
    }

    public UUID getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public LocalDate getServiceDate() {return serviceDate;}

    public void setServiceDate(LocalDate serviceDate) {this.serviceDate = serviceDate;}

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public RepairEstimate getEstimate() {
        return estimate;
    }

    public void setEstimate(RepairEstimate estimate) {
        this.estimate = estimate;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public ServiceRequestState getState() {
        return state;
    }

    public void setState(ServiceRequestState state) {
        this.state = state;
    }

    public ServiceRequestStateType getStateType() {
        return state.getStateType();
    }

    // State transition methods

    /**
     * Provides an estimate for this service request.
     * @param estimate The repair estimate
     * @throws IllegalStateException if providing an estimate is not allowed in the current state
     */
    public void provideEstimate(RepairEstimate estimate) throws IllegalStateException {
        this.state = this.state.provideEstimate(this, estimate);
    }

    /**
     * Accepts the estimate for this service request.
     * @throws IllegalStateException if accepting the estimate is not allowed in the current state
     */
    public void acceptEstimate() throws IllegalStateException {
        this.state = this.state.acceptEstimate(this);
    }

    /**
     * Rejects the estimate for this service request.
     * @throws IllegalStateException if rejecting the estimate is not allowed in the current state
     */
    public void rejectEstimate() throws IllegalStateException {
        this.state = this.state.rejectEstimate(this);
    }

    /**
     * Starts the service for this request.
     * @throws IllegalStateException if starting the service is not allowed in the current state
     */
    public void startService() throws IllegalStateException {
        this.state = this.state.startService(this);
    }

    /**
     * Completes the service for this request.
     * @throws IllegalStateException if completing the service is not allowed in the current state
     */
    public void completeService() throws IllegalStateException {
        this.state = this.state.completeService(this);
    }

    /**
     * Creates a report for this service request.
     * @param report The service report
     * @throws IllegalStateException if creating a report is not allowed in the current state
     */
    public void createReport(Report report) throws IllegalStateException {
        this.state.createReport(this, report);
        this.report = report;
    }
}