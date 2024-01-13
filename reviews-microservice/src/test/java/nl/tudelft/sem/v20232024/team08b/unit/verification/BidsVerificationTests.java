package nl.tudelft.sem.v20232024.team08b.unit.verification;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.application.verification.BidsVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictException;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class BidsVerificationTests {

    private final UsersVerification usersVerification = Mockito.mock(UsersVerification.class);
    private final TrackPhaseCalculator trackPhaseCalculator = Mockito.mock(TrackPhaseCalculator.class);
    private final ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);

    private final BidsVerification bidsVerification = new BidsVerification(
      usersVerification,
      trackPhaseCalculator,
      externalRepository
    );

    Long requesterID = 1L;
    Long paperID = 2L;
    Long reviewerID = 3L;

    @Test
    void verifyPermissionToAccessBid_ByReviewer() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);

        assertDoesNotThrow(() -> bidsVerification.verifyPermissionToAccessBid(requesterID, paperID));
    }

    @Test
    void verifyPermissionToAccessBid_ByChair() {

        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);

        assertDoesNotThrow(() -> bidsVerification.verifyPermissionToAccessBid(requesterID, paperID));
    }

    @Test
    void verifyPermissionToAccessBid_ForbiddenAccess() {

        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);

        assertThrows(ForbiddenAccessException.class,
                () -> bidsVerification.verifyPermissionToAccessBid(requesterID, paperID));
    }

    @Test
    void verifyPermissionToAccessAllBids_ValidRequester() throws NotFoundException {
        when(externalRepository.getSubmission(paperID)).thenReturn(new Submission());
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        assertDoesNotThrow(() -> bidsVerification.verifyPermissionToAccessAllBids(requesterID, paperID));
    }

    @Test
    void verifyPermissionToAccessAllBids_NotChair() throws NotFoundException {
        when(externalRepository.getSubmission(paperID)).thenReturn(new Submission());
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);

        Assertions.assertThrows(ForbiddenAccessException.class, () ->
                bidsVerification.verifyPermissionToAccessAllBids(requesterID, paperID));
    }

    @Test
    void verifyPermissionToAccessAllBids_ThrowsNotFoundException() throws NotFoundException {
        when(externalRepository.getSubmission(paperID)).thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class, () ->
                bidsVerification.verifyPermissionToAccessAllBids(requesterID, paperID));
    }

    @Test
    void verifyPermissionToAddBid_Successful() throws NotFoundException {
        Submission submission = new Submission();
        when(externalRepository.getSubmission(paperID)).thenReturn(submission);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);
        when(trackPhaseCalculator.getTrackPhase(submission.getEventId(), submission.getTrackId()))
                .thenReturn(TrackPhase.BIDDING);
        assertDoesNotThrow(() -> bidsVerification.verifyPermissionToAddBid(requesterID, paperID));
    }

    @Test
    void verifyPermissionToAddBid_NotAllowedRole() throws NotFoundException {
        Submission submission = new Submission();
        when(externalRepository.getSubmission(paperID)).thenReturn(submission);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(false);
        when(trackPhaseCalculator.getTrackPhase(submission.getEventId(), submission.getTrackId()))
                .thenReturn(TrackPhase.BIDDING);
        assertThrows(ForbiddenAccessException.class,
                () -> bidsVerification.verifyPermissionToAddBid(requesterID, paperID));
    }

    @Test
    void verifyPermissionToAddBid_NotAllowedPhase() throws NotFoundException {
        Submission submission = new Submission();
        when(externalRepository.getSubmission(paperID)).thenReturn(submission);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);
        when(trackPhaseCalculator.getTrackPhase(submission.getEventId(), submission.getTrackId()))
                .thenReturn(TrackPhase.REVIEWING);
        assertThrows(ConflictException.class,
                () -> bidsVerification.verifyPermissionToAddBid(requesterID, paperID));
    }

}
