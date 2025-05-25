package id.ac.ui.cs.advprog.perbaikiinaja.repository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Item;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ServiceRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private UserRepository userRepository; // We need this for Customer and Technician entities

    private Customer customer;
    private Technician technician;

    @BeforeEach
    void setUp() {
        // Create a customer
        customer = new Customer();
        customer.setFullName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPassword("password123");
        customer.setPhoneNumber("1234567890");
        customer.setAddress("123 Main St");
        customer.setRole("CUSTOMER");
        entityManager.persist(customer);

        // Create a technician
        technician = new Technician();
        technician.setFullName("Tech Smith");
        technician.setEmail("tech.smith@example.com");
        technician.setPassword("password123");
        technician.setPhoneNumber("0987654321");
        technician.setRole("TECHNICIAN");
        technician.setAddress("456 Tech St");
        technician.setCompletedJobs(0);
        technician.setTotalEarnings(0.0);
        entityManager.persist(technician);

        entityManager.flush();
    }

    @Test
    void testSave() {
        // Arrange
        Item item = new Item();
        item.setName("Smartphone");
        item.setCondition("Cracked screen");
        item.setIssueDescription("Screen doesn't respond to touch");
        entityManager.persist(item);

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setCustomer(customer);
        serviceRequest.setTechnician(technician);
        serviceRequest.setItem(item);
        serviceRequest.setProblemDescription("Phone screen is cracked and doesn't respond to touch");
        serviceRequest.setServiceDate(LocalDate.now().plusDays(3));

        // Act
        ServiceRequest savedRequest = serviceRequestRepository.save(serviceRequest);

        // Assert
        assertNotNull(savedRequest);
        assertNotNull(savedRequest.getId());
        assertEquals(customer.getId(), savedRequest.getCustomer().getId());
        assertEquals(technician.getId(), savedRequest.getTechnician().getId());
        assertEquals(item.getId(), savedRequest.getItem().getId());
        assertEquals("Phone screen is cracked and doesn't respond to touch", savedRequest.getProblemDescription());
    }

    @Test
    void testFindById() {
        // Arrange
        Item item = new Item();
        item.setName("Laptop");
        item.setCondition("Not powering on");
        item.setIssueDescription("No response when power button is pressed");
        entityManager.persist(item);

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setCustomer(customer);
        serviceRequest.setTechnician(technician);
        serviceRequest.setItem(item);
        serviceRequest.setProblemDescription("Laptop won't turn on");
        serviceRequest.setServiceDate(LocalDate.now().plusDays(2));

        entityManager.persist(serviceRequest);
        entityManager.flush();

        // Act
        Optional<ServiceRequest> foundRequest = serviceRequestRepository.findById(serviceRequest.getId());

        // Assert
        assertTrue(foundRequest.isPresent());
        assertEquals(serviceRequest.getId(), foundRequest.get().getId());
        assertEquals(customer.getId(), foundRequest.get().getCustomer().getId());
        assertEquals(technician.getId(), foundRequest.get().getTechnician().getId());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<ServiceRequest> foundRequest = serviceRequestRepository.findById(UUID.randomUUID());

        // Assert
        assertFalse(foundRequest.isPresent());
    }

    @Test
    void testFindAll() {
        // Arrange
        Item item1 = new Item();
        item1.setName("Smartphone");
        item1.setCondition("Cracked screen");
        item1.setIssueDescription("Screen doesn't respond to touch");
        entityManager.persist(item1);

        Item item2 = new Item();
        item2.setName("Laptop");
        item2.setCondition("Not powering on");
        item2.setIssueDescription("No response when power button is pressed");
        entityManager.persist(item2);

        ServiceRequest request1 = new ServiceRequest();
        request1.setCustomer(customer);
        request1.setTechnician(technician);
        request1.setItem(item1);
        request1.setProblemDescription("Phone screen is cracked");
        request1.setServiceDate(LocalDate.now().plusDays(3));

        ServiceRequest request2 = new ServiceRequest();
        request2.setCustomer(customer);
        request2.setTechnician(technician);
        request2.setItem(item2);
        request2.setProblemDescription("Laptop won't turn on");
        request2.setServiceDate(LocalDate.now().plusDays(2));

        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.flush();

        // Act
        Iterable<ServiceRequest> requests = serviceRequestRepository.findAll();
        List<ServiceRequest> requestList = new ArrayList<>();
        requests.forEach(requestList::add);

        // Assert
        assertFalse(requestList.isEmpty());
        assertTrue(requestList.size() >= 2);
        assertTrue(requestList.stream().anyMatch(r -> r.getId().equals(request1.getId())));
        assertTrue(requestList.stream().anyMatch(r -> r.getId().equals(request2.getId())));
    }

    @Test
    void testDelete() {
        // Arrange
        Item item = new Item();
        item.setName("Smartphone");
        item.setCondition("Cracked screen");
        item.setIssueDescription("Screen doesn't respond to touch");
        entityManager.persist(item);

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setCustomer(customer);
        serviceRequest.setTechnician(technician);
        serviceRequest.setItem(item);
        serviceRequest.setProblemDescription("Phone screen is cracked");
        serviceRequest.setServiceDate(LocalDate.now().plusDays(3));

        entityManager.persist(serviceRequest);
        entityManager.flush();

        // Act
        serviceRequestRepository.delete(serviceRequest);
        Optional<ServiceRequest> deletedRequest = serviceRequestRepository.findById(serviceRequest.getId());

        // Assert
        assertFalse(deletedRequest.isPresent());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Item item = new Item();
        item.setName("Smartphone");
        item.setCondition("Cracked screen");
        item.setIssueDescription("Screen doesn't respond to touch");
        entityManager.persist(item);

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setCustomer(customer);
        serviceRequest.setTechnician(technician);
        serviceRequest.setItem(item);
        serviceRequest.setProblemDescription("Phone screen is cracked");
        serviceRequest.setServiceDate(LocalDate.now().plusDays(3));

        entityManager.persist(serviceRequest);
        entityManager.flush();

        // Act
        serviceRequestRepository.deleteById(serviceRequest.getId());
        Optional<ServiceRequest> deletedRequest = serviceRequestRepository.findById(serviceRequest.getId());

        // Assert
        assertFalse(deletedRequest.isPresent());
    }

    @Test
    void testFindByTechnicianId() {
        // Arrange
        // Create another technician
        Technician anotherTechnician = new Technician();
        anotherTechnician.setFullName("Jane Tech");
        anotherTechnician.setEmail("jane.tech@example.com");
        anotherTechnician.setPassword("password123");
        anotherTechnician.setPhoneNumber("5551234567");
        anotherTechnician.setRole("TECHNICIAN");
        anotherTechnician.setAddress("789 Tech St");
        anotherTechnician.setCompletedJobs(0);
        anotherTechnician.setTotalEarnings(0.0);
        entityManager.persist(anotherTechnician);

        Item item1 = new Item();
        item1.setName("Smartphone");
        item1.setCondition("Cracked screen");
        item1.setIssueDescription("Screen doesn't respond to touch");
        entityManager.persist(item1);

        Item item2 = new Item();
        item2.setName("Laptop");
        item2.setCondition("Not powering on");
        item2.setIssueDescription("No response when power button is pressed");
        entityManager.persist(item2);

        Item item3 = new Item();
        item3.setName("Tablet");
        item3.setCondition("Battery issue");
        item3.setIssueDescription("Battery drains quickly");
        entityManager.persist(item3);

        // Create requests assigned to the first technician
        ServiceRequest request1 = new ServiceRequest();
        request1.setCustomer(customer);
        request1.setTechnician(technician);
        request1.setItem(item1);
        request1.setProblemDescription("Phone screen is cracked");
        request1.setServiceDate(LocalDate.now().plusDays(3));
        entityManager.persist(request1);

        ServiceRequest request2 = new ServiceRequest();
        request2.setCustomer(customer);
        request2.setTechnician(technician);
        request2.setItem(item2);
        request2.setProblemDescription("Laptop won't turn on");
        request2.setServiceDate(LocalDate.now().plusDays(2));
        entityManager.persist(request2);

        // Create a request assigned to the second technician
        ServiceRequest request3 = new ServiceRequest();
        request3.setCustomer(customer);
        request3.setTechnician(anotherTechnician);
        request3.setItem(item3);
        request3.setProblemDescription("Tablet battery drains quickly");
        request3.setServiceDate(LocalDate.now().plusDays(4));
        entityManager.persist(request3);

        entityManager.flush();

        // Act
        List<ServiceRequest> technicianRequests = serviceRequestRepository.findByTechnicianId(technician.getId());
        List<ServiceRequest> anotherTechnicianRequests = serviceRequestRepository.findByTechnicianId(anotherTechnician.getId());

        // Assert
        assertEquals(2, technicianRequests.size());
        assertTrue(technicianRequests.stream().anyMatch(r -> r.getId().equals(request1.getId())));
        assertTrue(technicianRequests.stream().anyMatch(r -> r.getId().equals(request2.getId())));

        assertEquals(1, anotherTechnicianRequests.size());
        assertTrue(anotherTechnicianRequests.stream().anyMatch(r -> r.getId().equals(request3.getId())));
    }

    @Test
    void testFindByCustomerId() {
        // Arrange
        // Create another customer
        Customer anotherCustomer = new Customer();
        anotherCustomer.setFullName("Jane Doe");
        anotherCustomer.setEmail("jane.doe@example.com");
        anotherCustomer.setPassword("password123");
        anotherCustomer.setPhoneNumber("9876543210");
        anotherCustomer.setAddress("789 Other St");
        anotherCustomer.setRole("CUSTOMER");
        entityManager.persist(anotherCustomer);

        Item item1 = new Item();
        item1.setName("Smartphone");
        item1.setCondition("Cracked screen");
        item1.setIssueDescription("Screen doesn't respond to touch");
        entityManager.persist(item1);

        Item item2 = new Item();
        item2.setName("Laptop");
        item2.setCondition("Not powering on");
        item2.setIssueDescription("No response when power button is pressed");
        entityManager.persist(item2);

        Item item3 = new Item();
        item3.setName("Tablet");
        item3.setCondition("Battery issue");
        item3.setIssueDescription("Battery drains quickly");
        entityManager.persist(item3);

        // Create requests from the first customer
        ServiceRequest request1 = new ServiceRequest();
        request1.setCustomer(customer);
        request1.setTechnician(technician);
        request1.setItem(item1);
        request1.setProblemDescription("Phone screen is cracked");
        request1.setServiceDate(LocalDate.now().plusDays(3));
        entityManager.persist(request1);

        ServiceRequest request2 = new ServiceRequest();
        request2.setCustomer(customer);
        request2.setTechnician(technician);
        request2.setItem(item2);
        request2.setProblemDescription("Laptop won't turn on");
        request2.setServiceDate(LocalDate.now().plusDays(2));
        entityManager.persist(request2);

        // Create a request from the second customer
        ServiceRequest request3 = new ServiceRequest();
        request3.setCustomer(anotherCustomer);
        request3.setTechnician(technician);
        request3.setItem(item3);
        request3.setProblemDescription("Tablet battery drains quickly");
        request3.setServiceDate(LocalDate.now().plusDays(4));
        entityManager.persist(request3);

        entityManager.flush();

        // Act
        List<ServiceRequest> customerRequests = serviceRequestRepository.findByCustomerId(customer.getId());
        List<ServiceRequest> anotherCustomerRequests = serviceRequestRepository.findByCustomerId(anotherCustomer.getId());

        // Assert
        assertEquals(2, customerRequests.size());
        assertTrue(customerRequests.stream().anyMatch(r -> r.getId().equals(request1.getId())));
        assertTrue(customerRequests.stream().anyMatch(r -> r.getId().equals(request2.getId())));

        assertEquals(1, anotherCustomerRequests.size());
        assertTrue(anotherCustomerRequests.stream().anyMatch(r -> r.getId().equals(request3.getId())));
    }
}