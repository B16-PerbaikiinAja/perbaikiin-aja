package id.ac.ui.cs.advprog.perbaikiinaja.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewRequestDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewResponseDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.TechnicianSelectionDto;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.service.review.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;
    private User mockAdmin;
    private User mockTechnician;

    private Review review1;
    private Review review2;
    private ReviewResponseDto reviewResponseDto1;
    private ReviewResponseDto reviewResponseDto2;
    private UUID userId;
    private UUID adminId;
    private UUID technicianId1;
    private UUID reviewId1;


    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        mockUser = mock(Customer.class);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getFullName()).thenReturn("Test User");


        adminId = UUID.randomUUID();
        mockAdmin = mock(User.class); // Generic user for admin, or Admin.class if specific
        when(mockAdmin.getId()).thenReturn(adminId);
        when(mockAdmin.getFullName()).thenReturn("Admin User");


        technicianId1 = UUID.randomUUID();
        mockTechnician = mock(Technician.class);
        when(mockTechnician.getId()).thenReturn(technicianId1);
        when(mockTechnician.getFullName()).thenReturn("Good Technician");


        reviewId1 = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        review1 = Review.builder()
                .id(reviewId1)
                .userId(userId)
                // userName and technicianName are not part of Review model
                .technicianId(technicianId1)
                .comment("Great service!")
                .rating(5)
                .createdAt(now.minusDays(1))
                .updatedAt(now.minusDays(1))
                .build();

        review2 = Review.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID()) // Different user
                .technicianId(technicianId1)
                .comment("Okay service.")
                .rating(3)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Mock service calls for DTO transformation
        when(reviewService.getUserById(review1.getUserId())).thenReturn(mockUser);
        when(reviewService.getUserById(review1.getTechnicianId())).thenReturn(mockTechnician);

        User anotherUserMock = mock(Customer.class);
        when(anotherUserMock.getId()).thenReturn(review2.getUserId());
        when(anotherUserMock.getFullName()).thenReturn("Another User");
        when(reviewService.getUserById(review2.getUserId())).thenReturn(anotherUserMock);
        when(reviewService.getUserById(review2.getTechnicianId())).thenReturn(mockTechnician);


        reviewResponseDto1 = new ReviewResponseDto(
            review1.getId(), review1.getUserId(), "Test User",
            review1.getTechnicianId(), "Good Technician",
            review1.getComment(), review1.getRating(),
            review1.getCreatedAt(), review1.getUpdatedAt(), true 
        );

        reviewResponseDto2 = new ReviewResponseDto(
            review2.getId(), review2.getUserId(), "Another User",
            review2.getTechnicianId(), "Good Technician",
            review2.getComment(), review2.getRating(),
            review2.getCreatedAt(), review2.getUpdatedAt(), false 
        );
    }

    private Authentication getMockAuthentication(User principal, String role) {
        return new UsernamePasswordAuthenticationToken(
            principal,
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }


    @Test
    void getAllReviews_shouldReturnListOfReviewResponseDtos() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(mockUser, "CUSTOMER"));
        when(reviewService.getAllReviews()).thenReturn(List.of(review1, review2));

        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(review1.getId().toString())))
                .andExpect(jsonPath("$[0].comment", is("Great service!")))
                .andExpect(jsonPath("$[0].canEditDelete", is(true))) 
                .andExpect(jsonPath("$[1].id", is(review2.getId().toString())))
                .andExpect(jsonPath("$[1].comment", is("Okay service.")))
                .andExpect(jsonPath("$[1].canEditDelete", is(false))); 
    }

    @Test
    @WithMockUser(username="user@example.com", roles="CUSTOMER")
    void createReview_whenAuthenticated_shouldReturnCreatedReview() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(mockUser, "CUSTOMER"));
        ReviewRequestDto requestDto = new ReviewRequestDto(technicianId1, "Fantastic work!", 5);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        when(reviewService.createReview(eq(userId), reviewCaptor.capture())).thenAnswer(invocation -> {
            Review capturedReview = invocation.getArgument(1);
            Review savedReview = Review.builder()
                                    .id(UUID.randomUUID()) 
                                    .userId(userId) 
                                    .technicianId(capturedReview.getTechnicianId())
                                    .comment(capturedReview.getComment())
                                    .rating(capturedReview.getRating())
                                    .createdAt(LocalDateTime.now())
                                    .updatedAt(LocalDateTime.now())
                                    .build();
             when(reviewService.getUserById(savedReview.getUserId())).thenReturn(mockUser);
             when(reviewService.getUserById(savedReview.getTechnicianId())).thenReturn(mockTechnician);
            return savedReview;
        });


        mockMvc.perform(post("/reviews")
                        .with(csrf()) 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment", is("Fantastic work!")))
                .andExpect(jsonPath("$.rating", is(5)))
                .andExpect(jsonPath("$.technicianId", is(technicianId1.toString())))
                .andExpect(jsonPath("$.userId", is(userId.toString()))) 
                .andExpect(jsonPath("$.canEditDelete", is(true))); 

        Review capturedForService = reviewCaptor.getValue();
        assertEquals(requestDto.getTechnicianId(), capturedForService.getTechnicianId());
        assertEquals(requestDto.getComment(), capturedForService.getComment());
        assertEquals(requestDto.getRating(), capturedForService.getRating());
        assertNull(capturedForService.getUserId()); 
    }


    @Test
    void createReview_whenNotAuthenticated_shouldReturnUnauthorized() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(null);
        ReviewRequestDto requestDto = new ReviewRequestDto(technicianId1, "This won't work", 1);

        mockMvc.perform(post("/reviews")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized()) 
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("User must be authenticated to create a review", ((ResponseStatusException)result.getResolvedException()).getReason()));

    }


    @Test
    @WithMockUser(username="user@example.com", roles="CUSTOMER")
    void createReview_withInvalidData_shouldReturnBadRequest() throws Exception {
         SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(mockUser, "CUSTOMER"));
        ReviewRequestDto requestDto = new ReviewRequestDto(null, "Short", 7); 

        when(reviewService.createReview(eq(userId), any(Review.class)))
            .thenThrow(new RuntimeException("Validation failed or some other bad request issue"));


        mockMvc.perform(post("/reviews")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest()) 
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException));
    }


    @Test
    @WithMockUser(username="user@example.com", roles="CUSTOMER")
    void updateReview_whenAuthenticatedAndAuthor_shouldReturnUpdatedReview() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(mockUser, "CUSTOMER")); 
        ReviewRequestDto requestDto = new ReviewRequestDto(review1.getTechnicianId(), "Updated comment!", 4);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        when(reviewService.updateReview(eq(userId), eq(reviewId1), reviewCaptor.capture())).thenAnswer(invocation -> {
            Review originalReview = review1; 
            Review detailsToUpdate = invocation.getArgument(2);
            originalReview.setComment(detailsToUpdate.getComment());
            originalReview.setRating(detailsToUpdate.getRating());
            originalReview.setUpdatedAt(LocalDateTime.now());
             when(reviewService.getUserById(originalReview.getUserId())).thenReturn(mockUser);
             when(reviewService.getUserById(originalReview.getTechnicianId())).thenReturn(mockTechnician);
            return originalReview;
        });

        mockMvc.perform(put("/reviews/{id}", reviewId1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(reviewId1.toString())))
                .andExpect(jsonPath("$.comment", is("Updated comment!")))
                .andExpect(jsonPath("$.rating", is(4)))
                .andExpect(jsonPath("$.canEditDelete", is(true)));

        Review capturedForService = reviewCaptor.getValue();
        assertEquals(requestDto.getTechnicianId(), capturedForService.getTechnicianId());
        assertEquals(requestDto.getComment(), capturedForService.getComment());
        assertEquals(requestDto.getRating(), capturedForService.getRating());
    }

    @Test
    @WithMockUser(username="otheruser@example.com", roles="CUSTOMER")
    void updateReview_whenNotAuthor_shouldReturnForbidden() throws Exception {
        User otherUser = mock(Customer.class);
        UUID otherUserId = UUID.randomUUID();
        when(otherUser.getId()).thenReturn(otherUserId);
        SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(otherUser, "CUSTOMER"));

        ReviewRequestDto requestDto = new ReviewRequestDto(review1.getTechnicianId(), "Attempted update", 3);
        when(reviewService.updateReview(eq(otherUser.getId()), eq(reviewId1), any(Review.class)))
                .thenThrow(new RuntimeException("You are not authorized to update this review.")); 

        mockMvc.perform(put("/reviews/{id}", reviewId1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("You are not authorized to update this review.", ((ResponseStatusException)result.getResolvedException()).getReason()));
    }

    @Test
    @WithMockUser(username="user@example.com", roles="CUSTOMER")
    void updateReview_notFound_shouldReturnNotFound() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(mockUser, "CUSTOMER"));
        UUID nonExistentReviewId = UUID.randomUUID();
        ReviewRequestDto requestDto = new ReviewRequestDto(technicianId1, "Update", 4);

        when(reviewService.updateReview(eq(userId), eq(nonExistentReviewId), any(Review.class)))
                .thenThrow(new RuntimeException("Review not found with ID: " + nonExistentReviewId));

        mockMvc.perform(put("/reviews/{id}", nonExistentReviewId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Review not found with ID: " + nonExistentReviewId, ((ResponseStatusException)result.getResolvedException()).getReason()));
    }


    @Test
    @WithMockUser(username="user@example.com", roles="CUSTOMER")
    void deleteReview_whenAuthenticatedAndAuthor_shouldReturnNoContent() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(mockUser, "CUSTOMER")); 
        doNothing().when(reviewService).deleteReview(reviewId1, userId);

        mockMvc.perform(delete("/reviews/{id}", reviewId1).with(csrf()))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(reviewId1, userId);
    }

    @Test
    @WithMockUser(username="otheruser@example.com", roles="CUSTOMER")
    void deleteReview_whenNotAuthor_shouldReturnForbidden() throws Exception {
        User otherUser = mock(Customer.class);
        UUID otherUserId = UUID.randomUUID();
        when(otherUser.getId()).thenReturn(otherUserId);
        SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(otherUser, "CUSTOMER"));

        doThrow(new RuntimeException("You are not authorized to delete this review."))
                .when(reviewService).deleteReview(reviewId1, otherUserId);

        mockMvc.perform(delete("/reviews/{id}", reviewId1).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("You are not authorized to delete this review.", ((ResponseStatusException)result.getResolvedException()).getReason()));
    }

    @Test
    @WithMockUser(username="admin@example.com", roles="ADMIN")
    void deleteReviewAsAdmin_shouldReturnNoContent() throws Exception {
         SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(mockAdmin, "ADMIN"));
        doNothing().when(reviewService).deleteReviewAsAdmin(reviewId1);

        mockMvc.perform(delete("/reviews/admin/{id}", reviewId1).with(csrf()))
                .andExpect(status().isNoContent());
        verify(reviewService).deleteReviewAsAdmin(reviewId1);
    }

    @Test
    @WithMockUser(username="admin@example.com", roles="ADMIN")
    void deleteReviewAsAdmin_notFound_shouldReturnNotFound() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(mockAdmin, "ADMIN"));
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new RuntimeException("Review not found with ID: " + nonExistentId))
            .when(reviewService).deleteReviewAsAdmin(nonExistentId);

        mockMvc.perform(delete("/reviews/admin/{id}", nonExistentId).with(csrf()))
            .andExpect(status().isNotFound());
    }


    @Test
    void getReviewsByTechnician_shouldReturnListOfReviewResponseDtos() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(mockUser, "CUSTOMER"));
        when(reviewService.getReviewsForTechnician(technicianId1)).thenReturn(List.of(review1, review2));

        mockMvc.perform(get("/reviews/technician/{technicianId}", technicianId1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].technicianId", is(technicianId1.toString())))
                .andExpect(jsonPath("$[1].technicianId", is(technicianId1.toString())));
    }

    @Test
    void getReviewsByTechnician_technicianNotFound_shouldReturnNotFound() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(mockUser, "CUSTOMER"));
        UUID nonExistentTechnicianId = UUID.randomUUID();
        when(reviewService.getReviewsForTechnician(nonExistentTechnicianId))
                .thenThrow(new RuntimeException("Technician not found or user is not a technician with ID: " + nonExistentTechnicianId));

        mockMvc.perform(get("/reviews/technician/{technicianId}", nonExistentTechnicianId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Technician not found or user is not a technician with ID: " + nonExistentTechnicianId, ((ResponseStatusException)result.getResolvedException()).getReason()));
    }


    @Test
    @WithMockUser(username="user@example.com", roles="CUSTOMER") 
    void getAvailableTechnicians_shouldReturnListOfTechnicianSelectionDtos() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getMockAuthentication(mockUser, "CUSTOMER"));
        TechnicianSelectionDto techDto1 = new TechnicianSelectionDto(technicianId1, "Good Technician");
        TechnicianSelectionDto techDto2 = new TechnicianSelectionDto(UUID.randomUUID(), "Another Technician");
        when(reviewService.getAvailableTechniciansForReview()).thenReturn(List.of(techDto1, techDto2));

        mockMvc.perform(get("/reviews/technicians/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(technicianId1.toString())))
                .andExpect(jsonPath("$[0].fullName", is("Good Technician")));
    }
}