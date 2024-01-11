package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.BidsService;
import nl.tudelft.sem.v20232024.team08b.application.VerificationService;
import nl.tudelft.sem.v20232024.team08b.domain.Bid;
import nl.tudelft.sem.v20232024.team08b.domain.BidID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;



public class BidsServiceTests {
    private BidRepository bidRepository = Mockito.mock(BidRepository.class);

    private VerificationService verificationService = Mockito.mock(VerificationService.class);

    private BidsService bidsService = new BidsService(bidRepository, null, verificationService);

    @BeforeEach
    void setUp() {
    }

    @Test
    public void testGetBidForPaperByReviewer() throws NotFoundException, ForbiddenAccessException {
        // Arrange
        Long requesterID = 1L;
        Long paperID = 2L;
        Long reviewerID = 3L;
        BidID bidID = new BidID(paperID, reviewerID);
        Bid bid = new Bid(paperID, reviewerID, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);

        when(bidRepository.findById(bidID)).thenReturn(Optional.of(bid));
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);

        // Act
        var result = bidsService.getBidForPaperByReviewer(requesterID, paperID, reviewerID);

        // Assert
        assertEquals(bid.getBid(), result);
    }

    @Test
    public void testGetBidForPaperByReviewerBidNotFound() {
        // Arrange
        Long requesterID = 1L;
        Long paperID = 2L;
        Long reviewerID = 3L;
        BidID bidID = new BidID(paperID, reviewerID);

        when(bidRepository.findById(bidID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bidsService.getBidForPaperByReviewer(requesterID, paperID, reviewerID));
        verify(bidRepository, times(1)).findById(bidID);
        verify(verificationService, never()).verifyRoleFromPaper(anyLong(), anyLong(), any(UserRole.class));
    }

    @Test
    public void testGetBidForPaperByReviewerForbiddenAccess() {
        // Arrange
        Long requesterID = 1L;
        Long paperID = 2L;
        Long reviewerID = 3L;
        BidID bidID = new BidID(paperID, reviewerID);

        when(bidRepository.findById(bidID)).thenReturn(Optional.of(new Bid()));
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);

        // Act & Assert
        assertThrows(ForbiddenAccessException.class, () -> bidsService.getBidForPaperByReviewer(requesterID, paperID, reviewerID));
    }
}
