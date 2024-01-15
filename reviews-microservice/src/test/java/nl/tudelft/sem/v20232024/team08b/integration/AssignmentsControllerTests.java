package nl.tudelft.sem.v20232024.team08b.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.AssignmentsService;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.controllers.AssignmentsController;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummaryWithID;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictException;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictOfInterestException;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class AssignmentsControllerTests {


    private AssignmentsService assignmentsService = Mockito.mock(AssignmentsService.class);
    private UsersVerification usersVerification = Mockito.mock(UsersVerification.class);
    private ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper();


    private MockMvc mockMvc;
    private Long requesterID = 1L;
    private Long reviewerID = 2L;
    private Long paperID = 3L;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AssignmentsController(assignmentsService)).build();
    }

    @Test
    void assignManualReturnsOk() throws Exception {

        doNothing().when(assignmentsService).assignManually(requesterID, reviewerID, paperID);

        mockMvc.perform(post("/papers/{paperID}/assignees/{reviewerID}", paperID, reviewerID)
                .param("requesterID", String.valueOf(requesterID))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        verify(assignmentsService).assignManually(requesterID, reviewerID, paperID);
    }

    @Test
    void assignManualReturnsNotFound() throws Exception {
        doThrow(new NotFoundException("Not found")).when(assignmentsService).assignManually(requesterID,
            reviewerID, paperID);

        mockMvc.perform(post("/papers/{paperID}/assignees/{reviewerID}", paperID, reviewerID)
                .param("requesterID", String.valueOf(requesterID))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(assignmentsService).assignManually(requesterID, reviewerID, paperID);
    }

    @Test
    void assignManualReturnsForbidden() throws Exception {

        doThrow(new IllegalAccessException("Forbidden")).when(assignmentsService).assignManually(requesterID,
            reviewerID, paperID);

        mockMvc.perform(post("/papers/{paperID}/assignees/{reviewerID}", paperID, reviewerID)
                .param("requesterID", String.valueOf(requesterID))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(assignmentsService).assignManually(requesterID, reviewerID, paperID);
    }

    @Test
    void assignManualReturnsConflict() throws Exception {

        doThrow(new ConflictOfInterestException("COI")).when(assignmentsService).assignManually(requesterID,
            reviewerID, paperID);

        mockMvc.perform(post("/papers/{paperID}/assignees/{reviewerID}", paperID, reviewerID)
                .param("requesterID", String.valueOf(requesterID))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());

        verify(assignmentsService).assignManually(requesterID, reviewerID, paperID);
    }

    @Test
    void assignManualReturnsInternalServerError() throws Exception {

        doThrow(new RuntimeException("Internal server error")).when(assignmentsService).assignManually(requesterID,
            reviewerID, paperID);


        mockMvc.perform(post("/papers/{paperID}/assignees/{reviewerID}", paperID, reviewerID)
                .param("requesterID", String.valueOf(requesterID))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        verify(assignmentsService).assignManually(requesterID, reviewerID, paperID);
    }

    @Test
    void assignmentsReturnsOk() throws Exception {

        List<Long> expectedAssignments = Arrays.asList(3L, 4L, 5L);
        when(assignmentsService.assignments(requesterID, paperID)).thenReturn(expectedAssignments);

        mockMvc.perform(get("/papers/{paperID}/assignees", paperID)
                .param("requesterID", String.valueOf(requesterID))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json("[3, 4, 5]"));

        verify(assignmentsService).assignments(requesterID, paperID);
    }

    @Test
    void assignmentsReturnsForbidden() throws Exception {

        when(assignmentsService.assignments(requesterID, paperID)).thenThrow(new IllegalAccessException("Forbidden"));

        mockMvc.perform(get("/papers/{paperID}/assignees", paperID)
                .param("requesterID", String.valueOf(requesterID))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(assignmentsService).assignments(requesterID, paperID);
    }

    @Test
    void assignmentsReturnsNotFound() throws Exception {

        when(assignmentsService.assignments(requesterID, paperID)).thenThrow(new IllegalCallerException("Not Found"));

        mockMvc.perform(get("/papers/{paperID}/assignees", paperID)
                .param("requesterID", String.valueOf(requesterID))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(assignmentsService).assignments(requesterID, paperID);
    }

    @Test
    void assignmentsReturnsInternalServerError() throws Exception {

        when(assignmentsService.assignments(requesterID, paperID)).thenThrow(new RuntimeException("Internal Server Error"));

        mockMvc.perform(get("/papers/{paperID}/assignees", paperID)
                .param("requesterID", String.valueOf(requesterID))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        verify(assignmentsService).assignments(requesterID, paperID);
    }

    @Test
    void getAssignedPapers_Successful() throws Exception {
        PaperSummaryWithID summaryWithID = new PaperSummaryWithID();
        summaryWithID.setAbstractSection("a");
        summaryWithID.setPaperID(paperID);
        summaryWithID.setTitle("t");

        List<PaperSummaryWithID> list = List.of(summaryWithID);
        String paperSummaryJSON = objectMapper.writeValueAsString(list);
        doReturn(list).when(assignmentsService).getAssignedPaper(requesterID);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/papers/by-reviewer")
                                .param("requesterID", Long.toString(requesterID))
                ).andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().json(paperSummaryJSON));

        verify(assignmentsService).getAssignedPaper(requesterID);
    }

    /**
     * Simulates an exception inside getAssignedPapers function and checks if
     * correct status code was returned.
     *
     * @param exception the exception to be thrown
     * @param expected the expected status code
     * @throws Exception method can throw exception
     */
    public void getAssignedPapers_Exceptions(Exception exception, int expected) throws Exception {
        long requesterID = 1L;

        doThrow(exception).when(assignmentsService).getAssignedPaper(requesterID);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/papers/by-reviewer")
                        .param("requesterID", Long.toString(requesterID))
        ).andExpect(MockMvcResultMatchers.status().is(expected));

        verify(assignmentsService).getAssignedPaper(requesterID);
    }

    @Test
    public void getAssignedPaper_NotFoundException() throws Exception {
        getAssignedPapers_Exceptions(new NotFoundException("User does not exist!"), 404);
    }

    @Test
    public void getAssignedPaper_OtherExceptions() throws Exception {
        getAssignedPapers_Exceptions(new RuntimeException(), 500);
    }

    public void testFinalizationSuccess() throws Exception {
        doNothing().when(assignmentsService).finalization(anyLong(), any(TrackID.class));

        mockMvc.perform(post("/conferences/{conferenceID}/tracks/{trackID}/finalization", 1L, 2L)
                        .param("requesterID", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(assignmentsService, times(1)).finalization(eq(3L),
                eq(new TrackID(1L, 2L)));
    }

    @Test
    public void testFinalizationConflictException() throws Exception {
        doThrow(new ConflictException()).when(assignmentsService).finalization(anyLong(), any(TrackID.class));

        mockMvc.perform(post("/conferences/{conferenceID}/tracks/{trackID}/finalization", 1L, 2L)
                        .param("requesterID", "3"))
                .andExpect(status().isConflict());

        verify(assignmentsService, times(1)).finalization(eq(3L),
                eq(new TrackID(1L, 2L)));
    }

    @Test
    public void testFinalizationNotFoundException() throws Exception {
        doThrow(new NotFoundException("")).when(assignmentsService).finalization(anyLong(), any(TrackID.class));

        mockMvc.perform(post("/conferences/{conferenceID}/tracks/{trackID}/finalization", 1L, 2L)
                        .param("requesterID", "3"))
                .andExpect(status().isNotFound());

        verify(assignmentsService, times(1)).finalization(eq(3L),
                eq(new TrackID(1L, 2L)));
    }

    @Test
    public void testFinalizationForbiddenAccessException() throws Exception {
        doThrow(new ForbiddenAccessException()).when(assignmentsService).finalization(anyLong(), any(TrackID.class));

        mockMvc.perform(post("/conferences/{conferenceID}/tracks/{trackID}/finalization", 1L, 2L)
                        .param("requesterID", "3"))
                .andExpect(status().isForbidden());

        verify(assignmentsService, times(1)).finalization(eq(3L),
                eq(new TrackID(1L, 2L)));
    }
}
