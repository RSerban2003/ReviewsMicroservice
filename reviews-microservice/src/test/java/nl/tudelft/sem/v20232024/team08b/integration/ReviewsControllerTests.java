package nl.tudelft.sem.v20232024.team08b.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.v20232024.team08b.application.ReviewsService;
import nl.tudelft.sem.v20232024.team08b.controllers.ReviewsController;
import nl.tudelft.sem.v20232024.team08b.domain.ConfidenceScore;
import nl.tudelft.sem.v20232024.team08b.domain.RecommendationScore;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewsControllerTests {
    MockMvc mockMvc;

    private ReviewsService reviewsService = Mockito.mock(ReviewsService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Review fakeReviewDTO;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                new ReviewsController(reviewsService)
        ).build();
        fakeReviewDTO = new nl.tudelft.sem.v20232024.team08b.dtos.review.Review(
                ConfidenceScore.BASIC,
                "Comment for author",
                "Confidential comment",
                RecommendationScore.STRONG_ACCEPT
        );
    }

    @Test
    public void submitReviewSuccessful() throws Exception {
        Long requesterID = 1L;
        Long paperID = 2L;

        // Convert the object into JSON to be passed as body
        String fakeReviewDTOJson = objectMapper.writeValueAsString(fakeReviewDTO);

        // Make sure nothing is done when the respective call to service is called
        doNothing().when(reviewsService).submitReview(fakeReviewDTO, requesterID, paperID);

        // Send the request to respective endpoint
        mockMvc.perform(
                MockMvcRequestBuilders.put("/papers/{paperID}/reviews", paperID.toString())
                        .param("requesterID", requesterID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fakeReviewDTOJson)
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        // Make sure the required call to the service was made
        verify(reviewsService).submitReview(fakeReviewDTO, requesterID, paperID);
    }

    /**
     * Simulates an exception inside submitReview function and checks if
     * correct status code was returned.
     *
     * @param exception the exception to be thrown
     * @param expected the expected status code
     * @throws Exception method can throw exception
     */
    public void submitReviewWithException(Exception exception, int expected) throws Exception {
        Long requesterID = 1L;
        Long paperID = 2L;

        // Convert the object into JSON to be passed as body
        String fakeReviewDTOJson = objectMapper.writeValueAsString(fakeReviewDTO);

        // Make sure nothing is done when the respective call to service is called
        doThrow(exception).when(reviewsService).submitReview(fakeReviewDTO, requesterID, paperID);

        // Send the request to respective endpoint
        mockMvc.perform(
                MockMvcRequestBuilders.put("/papers/{paperID}/reviews", paperID.toString())
                        .param("requesterID", requesterID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fakeReviewDTOJson)
        ).andExpect(MockMvcResultMatchers.status().is(expected));

        // Make sure the required call to the service was made
        verify(reviewsService).submitReview(fakeReviewDTO, requesterID, paperID);
    }

    @Test
    void testSubmitReviewNoSuchUser() throws Exception {
        submitReviewWithException(new IllegalCallerException(""), 404);
    }

    @Test
    void testSubmitReviewNoSuchPaper() throws Exception {
        submitReviewWithException(new IllegalArgumentException(""), 404);
    }

    @Test
    void testSubmitReviewUserNotReviewer() throws Exception {
        submitReviewWithException(new IllegalAccessException(""), 403);
    }

    @Test
    void testSubmitReviewInternalError() throws Exception {
        submitReviewWithException(new RuntimeException(""), 500);
    }
}
