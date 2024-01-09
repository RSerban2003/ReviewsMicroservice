package nl.tudelft.sem.v20232024.team08b.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.TracksService;
import nl.tudelft.sem.v20232024.team08b.controllers.TracksController;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TracksControllerTests {
    MockMvc mockMvc;
    final TracksService tracksService = Mockito.mock(TracksService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                new TracksController(tracksService)
        ).build();
    }

    /**
     * Simulates an exception inside getTrackPhase function and checks if
     * correct status code was returned.
     *
     * @param exception the exception to be thrown
     * @param expected the expected status code
     * @throws Exception method can throw exception
     */
    private void getPhase_WithException(Exception exception, int expected) throws Exception {
        Long requesterID = 1L;
        Long conferenceID = 2L;
        Long trackID = 3L;

        // Make sure correct exception is thrown when the respective call to service is called
        doThrow(exception).when(tracksService).getTrackPhase(requesterID, conferenceID, trackID);

        // Send the request to respective endpoint
        mockMvc.perform(
                MockMvcRequestBuilders.get("/conferences/{conferenceID}/tracks/{trackID}/phase",
                                conferenceID.toString(), trackID.toString())
                        .param("requesterID", requesterID.toString())
        ).andExpect(MockMvcResultMatchers.status().is(expected));

        // Make sure the required call to the service was made
        verify(tracksService).getTrackPhase(requesterID, conferenceID, trackID);
    }

    @Test
    void getPhase_NoSuchPaper() throws Exception {
        getPhase_WithException(new NotFoundException(""), 404);
    }

    @Test
    void getPhase_IllegalAccess() throws Exception {
        getPhase_WithException(new IllegalAccessException(""), 403);
    }

    @Test
    void getPhase_UnknownError() throws Exception {
        getPhase_WithException(new RuntimeException(""), 500);
    }

    @Test
    void getPhase_Successful() throws Exception {
        TrackPhase fakeTrackPhase = TrackPhase.BIDDING;
        String expectedJSON = objectMapper.writeValueAsString(fakeTrackPhase);

        Long requesterID = 1L;
        Long conferenceID = 2L;
        Long trackID = 3L;

        // Make sure correct exception is thrown when the respective call to service is called
        doReturn(fakeTrackPhase).when(tracksService).getTrackPhase(requesterID, conferenceID, trackID);

        // Send the request to respective endpoint
        mockMvc.perform(
                MockMvcRequestBuilders.get("/conferences/{conferenceID}/tracks/{trackID}/phase",
                                conferenceID.toString(), trackID.toString())
                        .param("requesterID", requesterID.toString())
                ).andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().json(expectedJSON));

        // Make sure the required call to the service was made
        verify(tracksService).getTrackPhase(requesterID, conferenceID, trackID);
    }

    /**
     * Simulates an exception inside getBiddingDeadline function and checks if
     * correct status code was returned.
     *
     * @param exception the exception to be thrown
     * @param expected the expected status code
     * @throws Exception method can throw exception
     */
    private void getBiddingDeadline_WithException(Exception exception, int expected) throws Exception {
        Long requesterID = 1L;
        Long conferenceID = 2L;
        Long trackID = 3L;

        // Make sure correct exception is thrown when the respective call to service is called
        doThrow(exception).when(tracksService).getBiddingDeadline(requesterID, conferenceID, trackID);

        // Send the request to respective endpoint
        mockMvc.perform(
                MockMvcRequestBuilders.get("/conferences/{conferenceID}/tracks/{trackID}/bidding-deadline",
                                conferenceID.toString(), trackID.toString())
                        .param("requesterID", requesterID.toString())
        ).andExpect(MockMvcResultMatchers.status().is(expected));

        // Make sure the required call to the service was made
        verify(tracksService).getTrackPhase(requesterID, conferenceID, trackID);
    }

    @Test
    void getBiddingDeadline_NoSuchPaper() throws Exception {
        getPhase_WithException(new NotFoundException(""), 404);
    }

    @Test
    void getBiddingDeadline_IllegalAccess() throws Exception {
        getPhase_WithException(new IllegalAccessException(""), 403);
    }

    @Test
    void getBiddingDeadline_UnknownError() throws Exception {
        getPhase_WithException(new RuntimeException(""), 500);
    }

    @Test
    void getBiddingDeadline_Successful() throws Exception {
        Date fakeDate = Date.valueOf(LocalDate.of(2012, 10, 2));
        String expectedJSON = objectMapper.writeValueAsString(fakeDate);

        Long requesterID = 1L;
        Long conferenceID = 2L;
        Long trackID = 3L;

        // Make sure correct exception is thrown when the respective call to service is called
        doReturn(fakeDate).when(tracksService).getBiddingDeadline(requesterID, conferenceID, trackID);

        // Send the request to respective endpoint
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/conferences/{conferenceID}/tracks/{trackID}/bidding-deadline",
                                        conferenceID.toString(), trackID.toString())
                                .param("requesterID", requesterID.toString())
                ).andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().json(expectedJSON));

        // Make sure the required call to the service was made
        verify(tracksService).getBiddingDeadline(requesterID, conferenceID, trackID);

    }
}
