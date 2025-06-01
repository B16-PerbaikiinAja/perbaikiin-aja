package id.ac.ui.cs.advprog.perbaikiinaja.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewRequestDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewResponseDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.TechnicianSelectionDto;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.services.auth.JwtService;
import id.ac.ui.cs.advprog.perbaikiinaja.service.review.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils; // Import ReflectionTestUtils


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
// Remove import for .with(user(...)) if not used elsewhere, or keep if other tests use it.
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService; // This is Spring Security's UserDetailsService

    @Autowired
    private ObjectMapper objectMapper;

    private User customerUser; // This is your custom User model
    private User technicianUser;
    private User adminUser;
    private Review review1;
    private Review review2;
    private ReviewRequestDto reviewRequestDto;
    private UUID customerId;
    private UUID technicianId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        customerUser = Customer.builder()
                .fullName("Test Customer")
                .email("customer@example.com") // This email will be used by @WithMockUser
                .password("password")
                .phoneNumber("1234567890")
                .address("Customer Address")
                .build();
        ReflectionTestUtils.setField(customerUser, "id", customerId);
        customerUser.setRole(UserRole.CUSTOMER.getValue());


        technicianId = UUID.randomUUID();
        technicianUser = Technician.builder()
                .fullName("Test Technician")
                .email("technician@example.com")
                .password("password")
                .phoneNumber("0987654321")
                .address("Technician Address")
                .build();
        ReflectionTestUtils.setField(technicianUser, "id", technicianId);
        technicianUser.setRole(UserRole.TECHNICIAN.getValue());

        UUID adminId = UUID.randomUUID();
        adminUser = id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin.builder()
                .fullName("Test Admin")
                .email("admin@example.com")
                .password("password")
                .phoneNumber("1122334455")
                .build();
        ReflectionTestUtils.setField(adminUser, "id", adminId);
        adminUser.setRole(UserRole.ADMIN.getValue());


        review1 = Review.builder()
                .id(UUID.randomUUID())
                .userId(customerId)
                .technicianId(technicianId)
                .comment("Great service!")
                .rating(5)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        review2 = Review.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID()) // Different user
                .technicianId(technicianId)
                .comment("Okay service.")
                .rating(3)
                .createdAt(LocalDateTime.now().minusHours(5))
                .updatedAt(LocalDateTime.now().minusHours(5))
                .build();

        reviewRequestDto = new ReviewRequestDto();
        reviewRequestDto.setTechnicianId(technicianId);
        reviewRequestDto.setComment("Excellent work!");
        reviewRequestDto.setRating(5);
    }

    // This helper might not be needed if using @WithMockUser and mocking UserDetailsService correctly
    // private org.springframework.security.core.userdetails.User createSpringUser(User userModel) {
    //     return new org.springframework.security.core.userdetails.User(
    //             userModel.getEmail(),
    //             userModel.getPassword(),
    //             userModel.getAuthorities()
    //     );
    // }



    @Test
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void deleteReviewAsAdmin_notAdminRole_shouldReturnForbidden() throws Exception {
        // @PreAuthorize("hasRole('ADMIN')") handles this
        mockMvc.perform(delete("/reviews/admin/{id}", review1.getId()))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "customer@example.com", authorities = {"ROLE_CUSTOMER"})
    void getReviewsByTechnician_technicianExists_shouldReturnReviews() throws Exception {
        when(userDetailsService.loadUserByUsername("customer@example.com")).thenReturn(customerUser);
        when(reviewService.getReviewsForTechnician(technicianId)).thenReturn(Arrays.asList(review1, review2));
        when(reviewService.getUserById(review1.getUserId())).thenReturn(customerUser);
        when(reviewService.getUserById(review1.getTechnicianId())).thenReturn(technicianUser);
        when(reviewService.getUserById(review2.getUserId())).thenReturn(null);
        when(reviewService.getUserById(review2.getTechnicianId())).thenReturn(technicianUser);


        mockMvc.perform(get("/reviews/technician/{technicianId}", technicianId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].technicianId").value(technicianId.toString()));

        verify(reviewService).getReviewsForTechnician(technicianId);
    }


    @Test
    @WithMockUser(username = "customer@example.com", authorities = {"ROLE_CUSTOMER"})
    void getAvailableTechnicians_authenticatedUser_shouldReturnTechnicians() throws Exception {
        when(userDetailsService.loadUserByUsername("customer@example.com")).thenReturn(customerUser);
        TechnicianSelectionDto techDto1 = new TechnicianSelectionDto(technicianUser.getId(), technicianUser.getFullName());
        when(reviewService.getAvailableTechniciansForReview()).thenReturn(Collections.singletonList(techDto1));

        mockMvc.perform(get("/reviews/technicians/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(technicianUser.getId().toString()))
                .andExpect(jsonPath("$[0].fullName").value(technicianUser.getFullName()));

        verify(reviewService).getAvailableTechniciansForReview();
    }

    @Test
    @WithAnonymousUser
    void getAvailableTechnicians_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
        // @PreAuthorize("isAuthenticated()") handles this
        mockMvc.perform(get("/reviews/technicians/available"))
                .andExpect(status().isUnauthorized());
    }
}