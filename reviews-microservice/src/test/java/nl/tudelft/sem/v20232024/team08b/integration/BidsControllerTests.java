package nl.tudelft.sem.v20232024.team08b.integration;

/*import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class BidsControllerTests {

    @BeforeEach
    void setup() {

    }
}*/

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.BidsService;
import nl.tudelft.sem.v20232024.team08b.controllers.BidsController;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Bid;
import nl.tudelft.sem.v20232024.team08b.dtos.review.BidByReviewer;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BidsControllerTests {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private BidsController bidsController;
    private BidsService bidsService = Mockito.mock(BidsService.class);
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        bidsController = new BidsController(bidsService);
        mockMvc = MockMvcBuilders.standaloneSetup(bidsController).build();
    }

    @Test
    public void testGetBidForPaperByReviewer() throws Exception {
        Long requesterID = 1L;
        Long paperID = 2L;
        Long reviewerID = 3L;

        Bid bid = nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW;
        when(bidsService.getBidForPaperByReviewer(requesterID, paperID, reviewerID)).thenReturn(bid);

        mockMvc.perform(get("/papers/{paperID}/bids/by-reviewer/{reviewerID}", paperID, reviewerID).param("requesterID", requesterID.toString())).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bid)));

    }

    @Test
    public void testGetBidForPaperByReviewerNotFound() throws Exception {
        Long requesterID = 1L;
        Long paperID = 2L;
        Long reviewerID = 3L;

        when(bidsService.getBidForPaperByReviewer(requesterID, paperID, reviewerID)).thenThrow(new NotFoundException(""));

        mockMvc.perform(get("/papers/{paperID}/bids/by-reviewer/{reviewerID}", paperID, reviewerID).param("requesterID", requesterID.toString()))
                .andExpect(status().isNotFound());

        verify(bidsService, times(1)).getBidForPaperByReviewer(requesterID, paperID, reviewerID);
    }

    @Test
    public void testGetBidForPaperByReviewerForbidden() throws Exception {
        Long requesterID = 1L;
        Long paperID = 2L;
        Long reviewerID = 3L;

        when(bidsService.getBidForPaperByReviewer(requesterID, paperID, reviewerID)).thenThrow(new ForbiddenAccessException());

        mockMvc.perform(get("/papers/{paperID}/bids/by-reviewer/{reviewerID}", paperID, reviewerID).param("requesterID", requesterID.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetBidsForPaperValidRequesterAndPaperReturnsBidsByReviewer() throws Exception {
        List<BidByReviewer> bids = new ArrayList<>();
        bids.add(new BidByReviewer(5L, Bid.CAN_REVIEW));
        bids.add(new BidByReviewer(4L, Bid.NOT_REVIEW));

        when(bidsService.getBidsForPaper(6L, 1L)).thenReturn(bids);

        var expected = new ArrayList<BidByReviewer>();
        expected.add(new BidByReviewer(5L,
                nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW));
        expected.add(new BidByReviewer(4L,
                nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.NOT_REVIEW));

        mockMvc.perform(get("/papers/{paperID}/bids", 1L).param("requesterID", String.valueOf(6L)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testGetBidsForPaperForbidden() throws Exception {
        when(bidsService.getBidsForPaper(6L, 1L)).thenThrow(new ForbiddenAccessException());

        mockMvc.perform(get("/papers/{paperID}/bids", 1L).param("requesterID", String.valueOf(6L)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetBidsForPaperNotFound() throws Exception {
        when(bidsService.getBidsForPaper(6L, 1L)).thenThrow(new NotFoundException(""));

        mockMvc.perform(get("/papers/{paperID}/bids", 1L).param("requesterID", String.valueOf(6L)))
                .andExpect(status().isNotFound());
    }
}
