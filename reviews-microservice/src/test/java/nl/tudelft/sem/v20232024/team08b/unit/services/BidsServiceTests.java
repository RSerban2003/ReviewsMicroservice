package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.BidsService;
import nl.tudelft.sem.v20232024.team08b.application.VerificationService;
import nl.tudelft.sem.v20232024.team08b.domain.Bid;
import nl.tudelft.sem.v20232024.team08b.domain.BidID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.BidByReviewer;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;



public class BidsServiceTests {
    private final BidRepository bidRepository = Mockito.mock(BidRepository.class);
    private final VerificationService verificationService = Mockito.mock(VerificationService.class);
    private final ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);
    private final BidsService bidsService = new BidsService(bidRepository, verificationService, externalRepository);

    @Test
    public void testGetBidForPaperByReviewer() throws NotFoundException, ForbiddenAccessException {
        Long requesterID = 1L;
        Long paperID = 2L;
        Long reviewerID = 3L;
        BidID bidID = new BidID(paperID, reviewerID);
        Bid bid = new Bid(paperID, reviewerID, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);

        when(bidRepository.findById(bidID)).thenReturn(Optional.of(bid));
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);

        var result = bidsService.getBidForPaperByReviewer(requesterID, paperID, reviewerID);

        assertEquals(bid.getBid(), result);
    }

    @Test
    public void testGetBidForPaperByReviewerBidNotFound() {
        Long requesterID = 1L;
        Long paperID = 2L;
        Long reviewerID = 3L;
        BidID bidID = new BidID(paperID, reviewerID);

        when(bidRepository.findById(bidID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bidsService.getBidForPaperByReviewer(requesterID, paperID, reviewerID));
        verify(bidRepository, times(1)).findById(bidID);
        verify(verificationService, never()).verifyRoleFromPaper(anyLong(), anyLong(), any(UserRole.class));
    }

    @Test
    public void testGetBidForPaperByReviewerForbiddenAccess() {
        Long requesterID = 1L;
        Long paperID = 2L;
        Long reviewerID = 3L;
        BidID bidID = new BidID(paperID, reviewerID);

        when(bidRepository.findById(bidID)).thenReturn(Optional.of(new Bid()));
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);

        assertThrows(ForbiddenAccessException.class, () -> bidsService.getBidForPaperByReviewer(requesterID, paperID, reviewerID));
    }

    @Test
    void testGetBidsForPaperValidRequesterAndPaperReturnsBidsByReviewer() throws NotFoundException, ForbiddenAccessException {
        List<Bid> bids = new ArrayList<>();
        bids.add(new Bid(1L, 5L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW));
        bids.add(new Bid(1L, 4L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.NOT_REVIEW));
        when(externalRepository.getSubmission(1L)).thenReturn(new Submission());
        when(verificationService.verifyRoleFromPaper(6L, 1L, UserRole.CHAIR)).thenReturn(true);
        when(bidRepository.findByPaperID(1L)).thenReturn(bids);

        List<BidByReviewer> result = bidsService.getBidsForPaper(6L, 1L);

        var expected = new ArrayList<BidByReviewer>();
        expected.add(new BidByReviewer(4L,
                nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.NOT_REVIEW));
        expected.add(new BidByReviewer(5L,
                nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW));
        Assertions.assertEquals(expected.size(), result.size());
        Assertions.assertTrue(expected.containsAll(result));
        Assertions.assertTrue(result.containsAll(expected));
    }

    @Test
    void testGetBidsForPaperInvalidRequesterThrowsForbiddenAccessException() throws NotFoundException {
        Long requesterID = 1L;
        Long paperID = 1L;
        when(externalRepository.getSubmission(paperID)).thenReturn(new Submission());
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);

        Assertions.assertThrows(ForbiddenAccessException.class, () -> bidsService.getBidsForPaper(requesterID, paperID));
    }

    @Test
    void testGetBidsForPaperPaperNotFoundThrowsNotFoundException() throws NotFoundException {
        Long requesterID = 1L;
        Long paperID = 1L;
        when(externalRepository.getSubmission(paperID)).thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class, () -> bidsService.getBidsForPaper(requesterID, paperID));
    }
}
