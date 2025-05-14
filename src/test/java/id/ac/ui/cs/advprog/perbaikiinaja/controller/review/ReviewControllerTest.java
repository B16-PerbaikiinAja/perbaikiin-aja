package id.ac.ui.cs.advprog.perbaikiinaja.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.perbaikiinaja.controller.review.ReviewController;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewRequestDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewResponseDto;
import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.service.review.ReviewService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateReview() throws Exception {
        // Preparing ReviewRequestDto for the test
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setUserId(100L);
        dto.setTechnicianId(200L);
        dto.setOrderId(1L);
        dto.setComment("Good job");
        dto.setRating(5);

        Review savedReview = Review.builder()
                .id(1L)
                .userId(100L)
                .technicianId(200L)
                .orderId(1L)
                .comment("Good job")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(reviewService.createReview(any(Long.class), any(Long.class), any(Review.class))).thenReturn(savedReview);

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.comment").value("Good job"));
    }

    @Test
    void testUpdateReview() throws Exception {
        // Preparing ReviewRequestDto for the test
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setUserId(100L);
        dto.setTechnicianId(200L);
        dto.setOrderId(1L);
        dto.setComment("Updated");
        dto.setRating(4);

        Review updatedReview = Review.builder()
                .id(1L)
                .userId(100L)
                .technicianId(200L)
                .orderId(1L)
                .comment("Updated")
                .rating(4)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(reviewService.updateReview(any(Long.class), any(Long.class), any(Review.class)))
                .thenReturn(updatedReview);

        mockMvc.perform(put("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Updated"));
    }

    @Test
    void testDeleteReview() throws Exception {
        // Simulating deletion with mock service call
        Mockito.doNothing().when(reviewService).deleteReview(1L, 100L);

        mockMvc.perform(delete("/reviews/1")
                        .param("userId", "100"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetReviewsForTechnician() throws Exception {
        Review review = Review.builder()
                .id(1L)
                .userId(100L)
                .technicianId(200L)
                .orderId(1L)
                .rating(5)
                .comment("Great!")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(reviewService.getReviewsForTechnician(200L))
                .thenReturn(List.of(review));

        mockMvc.perform(get("/reviews/technicians/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].technicianId").value(200))
                .andExpect(jsonPath("$[0].comment").value("Great!"));
    }
}
