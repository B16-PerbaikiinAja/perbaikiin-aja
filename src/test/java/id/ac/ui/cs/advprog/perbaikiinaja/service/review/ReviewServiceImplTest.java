package id.ac.ui.cs.advprog.perbaikiinaja.service.review;

import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.TechnicianSelectionDto;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.review.ReviewRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.validation.review.ReviewValidationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewValidationStrategy validationStrategy;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Spy
    private Customer customerUserSpy;
    @Spy
    private Technician technicianUserSpy;

    private Customer customerUserObj;
    private Technician technicianUserObj;

    private Review review;
    private UUID customerId;
    private UUID technicianId;
    private UUID reviewId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        technicianId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        customerUserObj = Customer.builder()
                .fullName("Test Customer")
                .email("customer@example.com")
                .password("password123") // Valid password
                .phoneNumber("1234567890")
                .address("Customer Address")
                .build();
        customerUserObj.setRole(UserRole.CUSTOMER.getValue());


        technicianUserObj = Technician.builder()
                .fullName("Test Technician")
                .email("technician@example.com")
                .password("password123") // Valid password
                .phoneNumber("0987654321")
                .address("Technician Address")
                .build();
        technicianUserObj.setRole(UserRole.TECHNICIAN.getValue());


        customerUserSpy = spy(customerUserObj);
        technicianUserSpy = spy(technicianUserObj);

        lenient().doReturn(customerId).when(customerUserSpy).getId();
        lenient().doReturn(technicianId).when(technicianUserSpy).getId();
        lenient().doReturn(UserRole.CUSTOMER.getValue()).when(customerUserSpy).getRole();
        lenient().doReturn(UserRole.TECHNICIAN.getValue()).when(technicianUserSpy).getRole();


        review = Review.builder()
                .id(reviewId)
                .userId(customerId)
                .technicianId(technicianId)
                .comment("Great service!")
                .rating(5)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    void createReview_success() {
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customerUserSpy));
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technicianUserSpy));
        when(reviewRepository.findByUserIdAndTechnicianId(customerId, technicianId)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        doNothing().when(validationStrategy).validate(any(Review.class));

        Review createdReview = reviewService.createReview(customerId, review);

        assertNotNull(createdReview);
        assertEquals(customerId, createdReview.getUserId());
        assertEquals(technicianId, createdReview.getTechnicianId());
        assertNotNull(createdReview.getCreatedAt());
        assertNotNull(createdReview.getUpdatedAt());
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(validationStrategy, times(1)).validate(any(Review.class));
    }

    @Test
    void createReview_userNotFound_throwsException() {
        when(userRepository.findById(customerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(customerId, review);
        });
        assertEquals("User not found", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_technicianNotFound_throwsException() {
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customerUserSpy));
        when(userRepository.findById(technicianId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(customerId, review);
        });
        assertEquals("Technician not found or user is not a technician", exception.getMessage());
    }

    @Test
    void createReview_technicianNotTechnicianRole_throwsException() {
        Customer notATechnicianObj = Customer.builder() // Create the actual object
                .fullName("Not Tech")
                .email("not@tech.com")
                .password("password123") // Fixed password
                .phoneNumber("12345678") // Valid phone number
                .address("a")
                .build();
        notATechnicianObj.setRole(UserRole.CUSTOMER.getValue()); // Set role on actual object

        Customer notATechnician = spy(notATechnicianObj); // Spy the object
        lenient().doReturn(technicianId).when(notATechnician).getId(); // Stub ID on the spy

        when(userRepository.findById(customerId)).thenReturn(Optional.of(customerUserSpy));
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(notATechnician));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(customerId, review);
        });
        assertEquals("Technician not found or user is not a technician", exception.getMessage());
    }


    @Test
    void createReview_alreadyReviewed_throwsException() {
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customerUserSpy));
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technicianUserSpy));
        when(reviewRepository.findByUserIdAndTechnicianId(customerId, technicianId)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(customerId, review);
        });
        assertEquals("You have already reviewed this technician.", exception.getMessage());
    }

    @Test
    void updateReview_success() {
        Review updatedDetails = Review.builder()
                .comment("Updated comment")
                .rating(4)
                .technicianId(technicianId)
                .build();

        review.setUserId(customerUserSpy.getId());

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        doNothing().when(validationStrategy).validate(any(Review.class));

        Review updatedReview = reviewService.updateReview(customerUserSpy.getId(), reviewId, updatedDetails);

        assertNotNull(updatedReview);
        assertEquals("Updated comment", updatedReview.getComment());
        assertEquals(4, updatedReview.getRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(validationStrategy, times(1)).validate(any(Review.class));
    }
    
    @Test
    void updateReview_notFound_throwsException() {
        Review updatedDetails = Review.builder().comment("Updated").rating(3).technicianId(technicianId).build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(customerId, reviewId, updatedDetails);
        });
        assertEquals("Review not found with ID: " + reviewId, exception.getMessage());
    }

    @Test
    void updateReview_notAuthorized_throwsException() {
        Review updatedDetails = Review.builder().comment("Updated").rating(3).technicianId(technicianId).build();
        UUID otherUserId = UUID.randomUUID();
        review.setUserId(customerId); 
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(otherUserId, reviewId, updatedDetails);
        });
        assertEquals("You are not authorized to update this review.", exception.getMessage());
    }
    
    @Test
    void updateReview_windowExpired_throwsException() {
        Review updatedDetails = Review.builder().comment("Updated").rating(3).technicianId(technicianId).build();
        review.setCreatedAt(LocalDateTime.now().minus(Duration.ofDays(8))); 
        review.setUserId(customerId); 
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(customerId, reviewId, updatedDetails);
        });
        assertTrue(exception.getMessage().contains("Review update window of"));
        assertTrue(exception.getMessage().contains("days has expired."));
    }

    @Test
    void updateReview_changingTechnicianId_throwsException() {
        Review updatedDetails = Review.builder()
                .comment("Updated comment")
                .rating(4)
                .technicianId(UUID.randomUUID()) 
                .build();
        review.setUserId(customerId);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.updateReview(customerId, reviewId, updatedDetails);
        });
        assertEquals("Cannot change the technician for an existing review.", exception.getMessage());
    }


    @Test
    void deleteReview_success() {
        review.setUserId(customerId); 
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).delete(review);

        assertDoesNotThrow(() -> reviewService.deleteReview(reviewId, customerId));
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void deleteReview_notFound_throwsException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(reviewId, customerId);
        });
        assertEquals("Review not found with ID: " + reviewId, exception.getMessage());
    }

    @Test
    void deleteReview_notAuthorized_throwsException() {
        UUID otherUserId = UUID.randomUUID();
        review.setUserId(customerId); 
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(reviewId, otherUserId);
        });
        assertEquals("You are not authorized to delete this review.", exception.getMessage());
    }

    @Test
    void getReviewsForTechnician_success() {
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technicianUserSpy));
        when(reviewRepository.findByTechnicianId(technicianId)).thenReturn(Collections.singletonList(review));

        List<Review> reviews = reviewService.getReviewsForTechnician(technicianId);

        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
        assertEquals(review, reviews.get(0));
    }

    @Test
    void getReviewsForTechnician_technicianNotFound_throwsException() {
        when(userRepository.findById(technicianId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewsForTechnician(technicianId);
        });
        assertEquals("Technician not found or user is not a technician with ID: " + technicianId, exception.getMessage());
    }
    
    @Test
    void getReviewsForTechnician_userIsNotTechnician_throwsException() {
        Customer notATechnicianObj = Customer.builder() // Create actual object
                .fullName("Not a Tech")
                .email("nota@tech.com")
                .password("password123") // Fixed password
                .phoneNumber("12345678") // Valid phone number
                .address("Some Address")
                .build();
        notATechnicianObj.setRole(UserRole.CUSTOMER.getValue()); // Set role on actual object

        Customer notATechnician = spy(notATechnicianObj); // Spy the object
        lenient().doReturn(technicianId).when(notATechnician).getId(); // Stub ID on spy, make lenient

        when(userRepository.findById(technicianId)).thenReturn(Optional.of(notATechnician));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewsForTechnician(technicianId);
        });
        assertEquals("Technician not found or user is not a technician with ID: " + technicianId, exception.getMessage());
    }


    @Test
    void calculateAverageRating_success() {
        Review review2 = Review.builder().rating(3).build();
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technicianUserSpy));
        when(reviewRepository.findByTechnicianId(technicianId)).thenReturn(Arrays.asList(review, review2));

        double averageRating = reviewService.calculateAverageRating(technicianId);
        assertEquals(4.0, averageRating, 0.001);
    }

    @Test
    void calculateAverageRating_noReviews_returnsZero() {
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technicianUserSpy));
        when(reviewRepository.findByTechnicianId(technicianId)).thenReturn(Collections.emptyList());

        double averageRating = reviewService.calculateAverageRating(technicianId);
        assertEquals(0.0, averageRating, 0.001);
    }

    @Test
    void getAllReviews_success() {
        when(reviewRepository.findAll()).thenReturn(Collections.singletonList(review));
        List<Review> reviews = reviewService.getAllReviews();
        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
    }

    @Test
    void deleteReviewAsAdmin_success() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).delete(review);

        assertDoesNotThrow(() -> reviewService.deleteReviewAsAdmin(reviewId));
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void deleteReviewAsAdmin_notFound_throwsException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReviewAsAdmin(reviewId);
        });
        assertEquals("Review not found with ID: " + reviewId, exception.getMessage());
    }

    @Test
    void getAvailableTechniciansForReview_success() {
        Technician techForListObj = Technician.builder()
                                .fullName("Available Tech")
                                .email("avail@tech.com")
                                .password("password123") // Fixed password
                                .phoneNumber("12345678") // Valid phone number
                                .address("a")
                                .build();
        techForListObj.setRole(UserRole.TECHNICIAN.getValue());

        Technician spiedTechForList = spy(techForListObj);
        UUID availableTechId = UUID.randomUUID();
        lenient().doReturn(availableTechId).when(spiedTechForList).getId();

        when(userRepository.findByRole(UserRole.TECHNICIAN.getValue())).thenReturn(Collections.singletonList(spiedTechForList));
        List<TechnicianSelectionDto> technicians = reviewService.getAvailableTechniciansForReview();

        assertFalse(technicians.isEmpty());
        assertEquals(1, technicians.size());
        assertEquals(availableTechId, technicians.get(0).getId());
        assertEquals("Available Tech", technicians.get(0).getFullName());
    }
    
    @Test
    void getUserById_found() {
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customerUserSpy));
        User foundUser = reviewService.getUserById(customerId);
        assertNotNull(foundUser);
        assertEquals(customerUserSpy, foundUser);
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(customerId)).thenReturn(Optional.empty());
        User foundUser = reviewService.getUserById(customerId);
        assertNull(foundUser);
    }
}