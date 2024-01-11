package nl.tudelft.sem.v20232024.team08b.unit.services;

import java.util.Optional;
import javassist.NotFoundException;
import javax.validation.Valid;
import nl.tudelft.sem.v20232024.team08b.application.VerificationService;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictOfInterestException;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.User;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUser;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUserTracksInner;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class VerificationServiceTests {
    final ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);
    final ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
    final TrackPhaseCalculator trackPhaseCalculator = Mockito.mock(TrackPhaseCalculator.class);

    final TrackRepository trackRepository = Mockito.mock(TrackRepository.class);
    private final VerificationService verificationService = Mockito.spy(
            new VerificationService(
                    externalRepository,
                    reviewRepository,
                    trackPhaseCalculator,
                    trackRepository
            )
    );
    private Submission fakeSubmission;
    private RolesOfUser fakeRolesOfUser;

    @BeforeEach
    void prepare() {
        fakeSubmission = new Submission();
        fakeSubmission.setTrackId(3L);

        RolesOfUserTracksInner innerReviewer = new RolesOfUserTracksInner();
        innerReviewer.setRoleName("PC Member");
        innerReviewer.setTrackId(2);
        innerReviewer.setEventId(4);

        List<RolesOfUserTracksInner> listOfTracks = new ArrayList<>();
        listOfTracks.add(innerReviewer);

        fakeRolesOfUser = new RolesOfUser();
        fakeRolesOfUser.setTracks(listOfTracks);
    }

    @Test
    void verifyCOI() throws NotFoundException, ConflictOfInterestException {
        when(externalRepository.getSubmission(1L)).thenReturn(fakeSubmission);
        //empty coi's list
        assertDoesNotThrow(() -> verificationService.verifyCOI(1L, 1L));
        User u1 = new User();
        u1.userId(5L);
        User u2 = new User();
        u2.userId(6L);
        User u3 = new User();
        u3.userId((7L));
        List<@Valid User> users = new ArrayList<>();
        users.add(u1);
        users.add(u2);
        users.add(u3);
        fakeSubmission.setConflictsOfInterest(users);
        //not in coi;s list
        assertDoesNotThrow(() -> verificationService.verifyCOI(1L, 1L));
        //coi exists
        assertThrows(ConflictOfInterestException.class, () -> {
            verificationService.verifyCOI(1L, 5L);
        });
    }

    @Test
    public void testVerifyIfTrackExists() throws NotFoundException {
        Long paperID = 123L;
        Submission submission = new Submission();
        submission.setTrackId(456L);
        submission.setEventId(789L);

        when(externalRepository.getSubmission(paperID)).thenReturn(submission);
        when(trackRepository.findById(any())).thenReturn(Optional.of(new Track()));

        verificationService.verifyIfTrackExists(paperID);

        verify(externalRepository, times(1)).getSubmission(paperID);
        verify(trackRepository, times(1)).findById(any());
        verify(verificationService, never()).insertTrack(anyLong(), anyLong());
    }

    @Test
    public void testVerifyIfTrackDoesNotExist() throws NotFoundException {
        Long paperID = 123L;
        Submission submission = new Submission();
        submission.setTrackId(456L);
        submission.setEventId(789L);

        when(externalRepository.getSubmission(paperID)).thenReturn(submission);
        when(trackRepository.findById(any())).thenReturn(Optional.empty());

        verificationService.verifyIfTrackExists(paperID);
        verify(verificationService, times(1)).insertTrack(anyLong(), anyLong());

    }

    @Test
    void testInsertTrack() {

        Long conferenceID = 1L;
        Long trackID = 2L;

        verificationService.insertTrack(conferenceID, trackID);

        Track expectedTrack = new Track();
        expectedTrack.setTrackID(new TrackID(conferenceID, trackID));
        expectedTrack.setReviewersHaveBeenFinalized(false);
        expectedTrack.setBiddingDeadline(null);

        verify(trackRepository).save(expectedTrack);
    }

    @Test
    void verifyPaperExists() throws NotFoundException {
        when(externalRepository.getSubmission(1L)).thenReturn(fakeSubmission);
        assertThat(verificationService.verifyPaper(1L)).isEqualTo(true);
    }

    @Test
    void verifyPaperDoesNotExist() throws NotFoundException {
        when(externalRepository.getSubmission(1L)).thenThrow(new NotFoundException(""));
        assertThat(verificationService.verifyPaper(1L)).isEqualTo(false);
    }

    @Test
    void verifyUserExists() throws NotFoundException {
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(verificationService.verifyRoleFromTrack(1L, 4L, 2L, UserRole.REVIEWER)).isEqualTo(true);
    }


    @Test
    void verifyUserExistsButInDifferentConference() throws NotFoundException {
        // This user IS a reviewer in a track with the same ID, but in a different conference
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(verificationService.verifyRoleFromTrack(1L, 3L, 2L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyUserExistsButInDifferentEvent() throws NotFoundException {
        // This user IS a reviewer in a track with the same ID, but in a different track
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(verificationService.verifyRoleFromTrack(1L, 4L, 1L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyUserExistsButBadRole() throws NotFoundException {
        // This user is in the same conference and track, but is not a reviewer

        // Construct a user in the same track, but he is a chair
        RolesOfUserTracksInner innerChair = new RolesOfUserTracksInner();
        innerChair.setRoleName("PC Chair");
        innerChair.setTrackId(2);
        innerChair.setEventId(4);

        // Add the user to the DTO
        List<RolesOfUserTracksInner> listOfTracks = new ArrayList<>();
        listOfTracks.add(innerChair);
        fakeRolesOfUser = new RolesOfUser();
        fakeRolesOfUser.setTracks(listOfTracks);

        // Mock the return
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(verificationService.verifyRoleFromTrack(1L, 3L, 2L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyUserDoesNotExist() throws NotFoundException {
        when(externalRepository.getRolesOfUser(1L)).thenThrow(new NotFoundException(""));
        assertThat(verificationService.verifyRoleFromTrack(1L, 4L, 2L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyUser2UsersOneExists() throws NotFoundException {
        // Construct a user in the same track, but he is a chair
        RolesOfUserTracksInner innerChair = new RolesOfUserTracksInner();
        innerChair.setRoleName("PC Chair");
        innerChair.setTrackId(2);
        innerChair.setEventId(4);

        // Add the user to the DTO
        fakeRolesOfUser.getTracks().add(innerChair);
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(verificationService.verifyRoleFromTrack(1L, 4L, 2L, UserRole.REVIEWER))
                .isEqualTo(true);
    }

    @Test
    void verifyUser2UsersNoneExist() throws NotFoundException {
        // Construct a user in the same track, but he is a chair
        RolesOfUserTracksInner innerChair = new RolesOfUserTracksInner();
        innerChair.setRoleName("PC Chair");
        innerChair.setTrackId(2);
        innerChair.setEventId(4);

        // Add the user to the DTO
        fakeRolesOfUser.getTracks().clear();
        fakeRolesOfUser.getTracks().add(innerChair);
        fakeRolesOfUser.getTracks().add(innerChair);

        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(verificationService.verifyRoleFromTrack(1L, 4L, 2L, UserRole.REVIEWER))
                .isEqualTo(false);
    }

    @Test
    void userIsReviewerForPaper() {
        Long reviewerID = 1L;
        Long paperID = 1L;

        when(reviewRepository.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        assertThat(verificationService.isReviewerForPaper(reviewerID, paperID)).isEqualTo(true);
    }

    @Test
    void userIsNotReviewerForPaper() {
        Long reviewerID = 1L;
        Long paperID = 1L;

        when(reviewRepository.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);
        assertThat(verificationService.isReviewerForPaper(reviewerID, paperID)).isEqualTo(false);
    }

    @Test
    void verifyTrackPhase_NoSuchTrack() throws NotFoundException {
        when(
                trackPhaseCalculator.getTrackPhase(0L, 1L)
        ).thenThrow(new NotFoundException(""));
        assertThrows(NotFoundException.class, () ->
                verificationService.verifyTrackPhase(0L, 1L, List.of()));
    }

    @Test
    void verifyTrackPhase_NoSuchPhase() throws NotFoundException {
        // Assume current phase is bidding
        when(
                trackPhaseCalculator.getTrackPhase(0L, 1L)
        ).thenReturn(TrackPhase.BIDDING);

        // Assume allowed phases are reviewing and final. Then the method should throw
        assertThrows(IllegalAccessException.class, () ->
                verificationService.verifyTrackPhase(
                0L, 1L,
                List.of(TrackPhase.REVIEWING, TrackPhase.FINAL)
        ));
    }

    @Test
    void verifyTrackPhase_PhaseMatches() throws NotFoundException {
        // Assume current phase is bidding
        when(
                trackPhaseCalculator.getTrackPhase(0L, 1L)
        ).thenReturn(TrackPhase.BIDDING);

        // Assume allowed phases are reviewing and bidding. Then the method should not throw
        assertDoesNotThrow(() -> verificationService.verifyTrackPhase(
                0L, 1L,
                List.of(TrackPhase.REVIEWING, TrackPhase.BIDDING)
        ));
    }

    @Test
    void verifyTrackPhaseThePaperIsIn_Throws() throws NotFoundException, IllegalAccessException {
        Long conferenceID = fakeSubmission.getEventId();
        Long trackID = fakeSubmission.getTrackId();
        Long paperID = fakeSubmission.getSubmissionId();
        List<TrackPhase> acceptable = List.of(TrackPhase.REVIEWING);

        // When we want to figure out paper parent ID, we ask external repository
        when(
                externalRepository.getSubmission(paperID)
        ).thenReturn(fakeSubmission);

        // Assume verifyTrackPhase throws
        doThrow(new NotFoundException(""))
                .when(verificationService).verifyTrackPhase(conferenceID, trackID, acceptable);
        // Assume allowed phases are reviewing and bidding. Then the method should not throw
        assertThrows(NotFoundException.class, () ->
                verificationService.verifyTrackPhaseThePaperIsIn(
                        paperID,
                        acceptable
                ));
    }

    @Test
    void verifyTrackPhaseThePaperIsIn_AllWell() throws NotFoundException, IllegalAccessException {
        Long conferenceID = fakeSubmission.getEventId();
        Long trackID = fakeSubmission.getTrackId();
        Long paperID = fakeSubmission.getSubmissionId();
        List<TrackPhase> acceptable = List.of(TrackPhase.REVIEWING);

        // When we want to figure out paper parent ID, we ask external repository
        when(
                externalRepository.getSubmission(paperID)
        ).thenReturn(fakeSubmission);

        // Assume verifyTrackPhase throws
        doNothing()
                .when(verificationService).verifyTrackPhase(conferenceID, trackID, acceptable);
        // Assume allowed phases are reviewing and bidding. Then the method should not throw
        assertDoesNotThrow(() ->
                verificationService.verifyTrackPhaseThePaperIsIn(
                        paperID,
                        acceptable
                )
        );
    }

    @Test
    void verifyTrack_Yes() throws NotFoundException {
        when(externalRepository.getTrack(1L, 2L)).thenReturn(null);
        assertThat(verificationService.verifyTrack(1L, 2L)).isTrue();
    }

    @Test
    void verifyTrack_No() throws NotFoundException {
        when(externalRepository.getTrack(1L, 2L)).thenThrow(
                new NotFoundException("")
        );
        assertThat(verificationService.verifyTrack(1L, 2L)).isFalse();
    }

    @Test
    void verifyRoleFromPaper_Yes() throws NotFoundException {
        Submission fakeSubmission = new Submission();
        fakeSubmission.setEventId(1L);
        fakeSubmission.setTrackId(2L);

        when(externalRepository.getSubmission(3L)).thenReturn(fakeSubmission);
        doReturn(true).when(verificationService)
                .verifyRoleFromTrack(0L, 1L, 2L, UserRole.CHAIR);
        boolean result = verificationService.verifyRoleFromPaper(0L, 3L, UserRole.CHAIR);
        assertThat(result).isTrue();
    }

    @Test
    void verifyRoleFromPaper_No() throws NotFoundException {
        Submission fakeSubmission = new Submission();
        fakeSubmission.setEventId(1L);
        fakeSubmission.setTrackId(2L);

        when(externalRepository.getSubmission(3L)).thenReturn(fakeSubmission);
        doReturn(false).when(verificationService)
                .verifyRoleFromTrack(0L, 1L, 2L, UserRole.CHAIR);
        boolean result = verificationService.verifyRoleFromPaper(0L, 3L, UserRole.CHAIR);
        assertThat(result).isFalse();
    }

    @Test
    void verifyRoleFromPaper_NoSuchPaper() throws NotFoundException {
        when(externalRepository.getSubmission(3L)).thenThrow(
                new NotFoundException("")
        );
        boolean result = verificationService.verifyRoleFromPaper(0L, 3L, UserRole.CHAIR);
        assertThat(result).isFalse();
    }

    @Test
    void verifyIsAuthorToPaper_NoSuchPaper() throws NotFoundException {
        when(externalRepository.getSubmission(3L)).thenThrow(
                new NotFoundException("")
        );
        boolean result = verificationService.isAuthorToPaper(0L, 3L);
        assertThat(result).isFalse();
    }

    @Test
    void verifyIsAuthorToPaper_NotAuthor() throws NotFoundException {
        User user1 = new User();
        User user2 = new User();
        user1.setUserId(1L);
        user2.setUserId(2L);
        fakeSubmission.setAuthors(List.of(user1, user2));
        when(externalRepository.getSubmission(3L)).thenReturn(fakeSubmission);
        boolean result = verificationService.isAuthorToPaper(0L, 3L);
        assertThat(result).isFalse();
    }

    @Test
    void verifyIsAuthorToPaper() throws NotFoundException {
        User user1 = new User();
        User user2 = new User();
        user1.setUserId(1L);
        user2.setUserId(0L);
        fakeSubmission.setAuthors(List.of(user1, user2));
        when(externalRepository.getSubmission(3L)).thenReturn(fakeSubmission);
        boolean result = verificationService.isAuthorToPaper(0L, 3L);
        assertThat(result).isTrue();
    }
}