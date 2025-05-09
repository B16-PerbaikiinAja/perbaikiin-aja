package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.review.controller.ReviewController;
import id.ac.ui.cs.advprog.perbaikiinaja.review.service.ReviewService;
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
        Review review = Review.builder()
                .id(1L)
                .userId(100L)
                .technicianId(200L)
                .rating(5)
                .comment("Good job")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(reviewService.createReview(any(Review.class))).thenReturn(review);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.comment").value("Good job"));
    }

    @Test
    void testUpdateReview() throws Exception {
        Review review = Review.builder()
                .id(1L)
                .userId(100L)
                .technicianId(200L)
                .rating(4)
                .comment("Updated")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(reviewService.updateReview(any(Long.class), any(Review.class)))
                .thenReturn(review);

        mockMvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Updated"));
    }

    @Test
    void testDeleteReview() throws Exception {
        Mockito.doNothing().when(reviewService).deleteReview(1L, 100L);

        mockMvc.perform(delete("/api/reviews/1")
                        .param("userId", "100"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetReviewsForTechnician() throws Exception {
        Review review = Review.builder()
                .id(1L)
                .userId(100L)
                .technicianId(200L)
                .rating(5)
                .comment("Great!")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(reviewService.getReviewsForTechnician(200L))
                .thenReturn(List.of(review));

        mockMvc.perform(get("/api/reviews/technician/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].technicianId").value(200));
    }
}
