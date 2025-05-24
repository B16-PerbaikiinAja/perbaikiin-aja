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

    private User customerUser;
    private User technicianUser;
    private Review review;
    private Review updatedReviewDetails;
    private UUID customerId;
    private UUID technicianId;
    private UUID reviewId;

    private static final Duration REVIEW_UPDATE_WINDOW = Duration.ofDays(7);


    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        technicianId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        // Mock User objects
        customerUser = mock(Customer.class); // Use mock(Customer.class) for specific type if needed
        when(customerUser.getId()).thenReturn(customerId);
        when(customerUser.getFullName()).thenReturn("Test Customer");
        // Add other necessary when(customerUser.getX()).thenReturn(Y) as required by your service logic

        technicianUser = mock(Technician.class); // Use mock(Technician.class)
        when(technicianUser.getId()).thenReturn(technicianId);
        when(technicianUser.getFullName()).thenReturn("Test Technician");
        when(technicianUser.getRole()).thenReturn(UserRole.TECHNICIAN.getValue());
        // Add other necessary when(technicianUser.getX()).thenReturn(Y)

        review = Review.builder()
                .id(reviewId)
                .userId(customerId)
                .technicianId(technicianId)
                .comment("Original comment, long enough for validation.")
                .rating(5)
                .createdAt(LocalDateTime.now().minusDays(1)) // within update window
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        updatedReviewDetails = Review.builder()
                .technicianId(technicianId) // technicianId should match for update
                .comment("Updated comment, also sufficiently long.")
                .rating(4)
                .build();
    }

    @Test
    void createReview_success() {
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customerUser));
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technicianUser)); 
        when(reviewRepository.findByUserIdAndTechnicianId(customerId, technicianId)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review r = invocation.getArgument(0);
            if (r.getId() == null) r.setId(UUID.randomUUID()); 
            r.setCreatedAt(LocalDateTime.now()); 
            r.setUpdatedAt(LocalDateTime.now()); 
            return r;
        });
        doNothing().when(validationStrategy).validate(any(Review.class));


        Review reviewToCreate = Review.builder()
                                .technicianId(technicianId)
                                .comment("A good comment for creation")
                                .rating(5)
                                .build();

        Review created = reviewService.createReview(customerId, reviewToCreate);

        assertNotNull(created);
        assertEquals(customerId, created.getUserId());
        assertEquals(technicianId, created.getTechnicianId());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        verify(validationStrategy).validate(reviewToCreate);
        verify(reviewRepository).save(reviewToCreate);
    }

    @Test
    void createReview_userNotFound_throwsRuntimeException() {
        when(userRepository.findById(customerId)).thenReturn(Optional.empty());
        Review reviewToCreate = Review.builder().technicianId(technicianId).comment("test").rating(1).build();


        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(customerId, reviewToCreate);
        });
        assertEquals("User not found", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_technicianNotFound_throwsRuntimeException() {
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customerUser));
        when(userRepository.findById(technicianId)).thenReturn(Optional.empty()); 
        Review reviewToCreate = Review.builder().technicianId(technicianId).comment("test").rating(1).build();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(customerId, reviewToCreate);
        });
        assertEquals("Technician not found or user is not a technician", exception.getMessage());
    }

    @Test
    void createReview_reviewedUserIsNotTechnician_throwsRuntimeException() {
        User notATechnician = mock(Customer.class); // Mock a user that is not a Technician
        when(notATechnician.getId()).thenReturn(technicianId);
        when(notATechnician.getRole()).thenReturn(UserRole.CUSTOMER.getValue()); 

        when(userRepository.findById(customerId)).thenReturn(Optional.of(customerUser));
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(notATechnician));
        Review reviewToCreate = Review.builder().technicianId(technicianId).comment("test").rating(1).build();


        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(customerId, reviewToCreate);
        });
        assertEquals("Technician not found or user is not a technician", exception.getMessage());
    }

    @Test
    void createReview_alreadyReviewed_throwsRuntimeException() {
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customerUser));
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technicianUser));
        when(reviewRepository.findByUserIdAndTechnicianId(customerId, technicianId)).thenReturn(Optional.of(review)); 
        Review reviewToCreate = Review.builder().technicianId(technicianId).comment("test").rating(1).build();


        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(customerId, reviewToCreate);
        });
        assertEquals("You have already reviewed this technician.", exception.getMessage());
    }


    @Test
    void updateReview_success() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review); 
        doNothing().when(validationStrategy).validate(any(Review.class));

        Review result = reviewService.updateReview(customerId, reviewId, updatedReviewDetails);

        assertNotNull(result);
        assertEquals(updatedReviewDetails.getComment(), result.getComment());
        assertEquals(updatedReviewDetails.getRating(), result.getRating());
        verify(validationStrategy).validate(review); 
        verify(reviewRepository).save(review);
    }

    @Test
    void updateReview_notFound_throwsRuntimeException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(customerId, reviewId, updatedReviewDetails);
        });
        assertEquals("Review not found with ID: " + reviewId, exception.getMessage());
    }

    @Test
    void updateReview_unauthorizedUser_throwsRuntimeException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review)); 
        UUID otherUserId = UUID.randomUUID();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(otherUserId, reviewId, updatedReviewDetails);
        });
        assertEquals("You are not authorized to update this review.", exception.getMessage());
    }

    @Test
    void updateReview_windowExpired_throwsRuntimeException() {
        review.setCreatedAt(LocalDateTime.now().minus(REVIEW_UPDATE_WINDOW).minusHours(1)); 
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(customerId, reviewId, updatedReviewDetails);
        });
        assertEquals("Review update window of " + REVIEW_UPDATE_WINDOW.toDays() + " days has expired.", exception.getMessage());
    }

    @Test
    void updateReview_changingTechnicianId_throwsIllegalArgumentException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        updatedReviewDetails.setTechnicianId(UUID.randomUUID()); 

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.updateReview(customerId, reviewId, updatedReviewDetails);
        });
        assertEquals("Cannot change the technician for an existing review.", exception.getMessage());
    }

    @Test
    void deleteReview_success() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).delete(review);

        assertDoesNotThrow(() -> reviewService.deleteReview(reviewId, customerId));
        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_notFound_throwsRuntimeException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(reviewId, customerId);
        });
        assertEquals("Review not found with ID: " + reviewId, exception.getMessage());
    }

    @Test
    void deleteReview_unauthorizedUser_throwsRuntimeException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        UUID otherUserId = UUID.randomUUID();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(reviewId, otherUserId);
        });
        assertEquals("You are not authorized to delete this review.", exception.getMessage());
    }

    @Test
    void getReviewsForTechnician_success() {
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technicianUser));
        when(reviewRepository.findByTechnicianId(technicianId)).thenReturn(Collections.singletonList(review));

        List<Review> result = reviewService.getReviewsForTechnician(technicianId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(review, result.get(0));
    }

    @Test
    void getReviewsForTechnician_technicianNotFound_throwsRuntimeException() {
        when(userRepository.findById(technicianId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewsForTechnician(technicianId);
        });
        assertEquals("Technician not found or user is not a technician with ID: " + technicianId, exception.getMessage());
    }

     @Test
    void getReviewsForTechnician_userIsNotTechnician_throwsRuntimeException() {
        User notATechnician = mock(Customer.class);
        when(notATechnician.getId()).thenReturn(technicianId);
        when(notATechnician.getRole()).thenReturn(UserRole.CUSTOMER.getValue());

        when(userRepository.findById(technicianId)).thenReturn(Optional.of(notATechnician));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewsForTechnician(technicianId);
        });
        assertEquals("Technician not found or user is not a technician with ID: " + technicianId, exception.getMessage());
    }


    @Test
    void calculateAverageRating_success() {
        Review review2 = Review.builder().rating(3).technicianId(technicianId).build();
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technicianUser)); 
        when(reviewRepository.findByTechnicianId(technicianId)).thenReturn(Arrays.asList(review, review2));


        double average = reviewService.calculateAverageRating(technicianId);
        assertEquals(4.0, average, 0.001); 
    }

    @Test
    void calculateAverageRating_noReviews_returnsZero() {
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technicianUser)); 
        when(reviewRepository.findByTechnicianId(technicianId)).thenReturn(Collections.emptyList());

        double average = reviewService.calculateAverageRating(technicianId);
        assertEquals(0.0, average, 0.001);
    }

    @Test
    void getAllReviews_success() {
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(review, updatedReviewDetails));
        List<Review> result = reviewService.getAllReviews();
        assertEquals(2, result.size());
    }

    @Test
    void deleteReviewAsAdmin_success() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).delete(review);
        assertDoesNotThrow(() -> reviewService.deleteReviewAsAdmin(reviewId));
        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReviewAsAdmin_notFound_throwsRuntimeException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> reviewService.deleteReviewAsAdmin(reviewId));
        assertEquals("Review not found with ID: " + reviewId, exception.getMessage());
    }

    @Test
    void getAvailableTechniciansForReview_success() {
        Technician tech1Mock = mock(Technician.class);
        when(tech1Mock.getId()).thenReturn(UUID.randomUUID());
        when(tech1Mock.getFullName()).thenReturn("Tech One");

        Technician tech2Mock = mock(Technician.class);
        when(tech2Mock.getId()).thenReturn(UUID.randomUUID());
        when(tech2Mock.getFullName()).thenReturn("Tech Two");


        when(userRepository.findByRole(UserRole.TECHNICIAN.getValue())).thenReturn(Arrays.asList(tech1Mock, tech2Mock));

        List<TechnicianSelectionDto> result = reviewService.getAvailableTechniciansForReview();

        assertEquals(2, result.size());
        assertEquals("Tech One", result.get(0).getFullName());
        assertEquals(tech1Mock.getId(), result.get(0).getId());
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customerUser));
        User result = reviewService.getUserById(customerId);
        assertEquals(customerUser, result);
    }

    @Test
    void getUserById_notFound_returnsNull() {
        when(userRepository.findById(customerId)).thenReturn(Optional.empty());
        User result = reviewService.getUserById(customerId);
        assertNull(result);
    }
}