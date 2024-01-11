package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.TracksService;
import nl.tudelft.sem.v20232024.team08b.application.VerificationService;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TracksServicesTests {
    private final VerificationService verificationService = Mockito.mock(VerificationService.class);
    private final TrackPhaseCalculator trackPhaseCalculator = Mockito.mock(TrackPhaseCalculator.class);
    private final TrackRepository trackRepository = Mockito.mock(TrackRepository.class);
    private final ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);
    private final TracksService tracksService = Mockito.spy(
            new TracksService(
                    verificationService,
                    trackPhaseCalculator,
                    trackRepository,
                    externalRepository
            )
    );
    private Long requesterID = 0L;
    private Long conferenceID = 1L;
    private Long trackID = 2L;
    private Track track;
    @BeforeEach
    void init() {
        track = new Track();
        // Assume that the user has no role
        when(
                verificationService.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.REVIEWER)
        ).thenReturn(false);
        when(
                verificationService.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR)
        ).thenReturn(false);
    }

    @Test
    void verifyIfUserCanAccessTrackNoSuchTrack() {
        when(
                verificationService.verifyTrack(conferenceID, trackID)
        ).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                tracksService.verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID));
    }

    @Test
    void insertTrackToOurDBValidInput() {
        Long conferenceID = 1L;
        Long trackID = 2L;

        assertDoesNotThrow(() -> tracksService.insertTrackToOurDB(conferenceID, trackID));

        verify(trackRepository).save(argThat(track -> track.getTrackID().getConferenceID().equals(conferenceID)
            && track.getTrackID().getTrackID().equals(trackID)
            && !track.getReviewersHaveBeenFinalized()
            && track.getBiddingDeadline() == null));
    }

    void applyRole(UserRole role) {
        when(
                verificationService.verifyRoleFromTrack(requesterID, conferenceID, trackID, role)
        ).thenReturn(true);
    }

    @Test
    void verifyIfUserCanAccessTrack_Reviewer() {
        when(
                verificationService.verifyTrack(conferenceID, trackID)
        ).thenReturn(true);

        // Assume the user is a reviewer
        applyRole(UserRole.REVIEWER);

        assertDoesNotThrow(() ->
                tracksService.verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID));
    }

    @Test
    void verifyIfUserCanAccessTrack_Chair() {
        when(
                verificationService.verifyTrack(conferenceID, trackID)
        ).thenReturn(true);

        // Assume the user is a chair
        applyRole(UserRole.CHAIR);

        assertDoesNotThrow(() ->
                tracksService.verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID));
    }

    @Test
    void verifyIfUserCanAccessTrack_Author() {
        when(
                verificationService.verifyTrack(conferenceID, trackID)
        ).thenReturn(true);

        // Assume the user is a chair
        applyRole(UserRole.AUTHOR);

        assertDoesNotThrow(() ->
                tracksService.verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID));
    }

    @Test
    void verifyIfUserCanAccessTrack_NoOne() {
        when(
                verificationService.verifyTrack(conferenceID, trackID)
        ).thenReturn(true);

        // Assume the user is neither chair nor reviewer

        assertThrows(IllegalAccessException.class, () ->
                tracksService.verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID));
    }

    @Test
    void getTrackPhase() throws NotFoundException, IllegalAccessException {
        // Assume that the current phase is bidding
        when(trackPhaseCalculator.getTrackPhase(conferenceID, trackID)).thenReturn(TrackPhase.BIDDING);

        // Assume that the provided input to function is valid
        doNothing().when(tracksService).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // Make sure, that the service returns the same result that the calculator returns
        assertThat(
                tracksService.getTrackPhase(requesterID, conferenceID, trackID)
        ).isEqualTo(TrackPhase.BIDDING);
        verify(tracksService).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);
    }

    @Test
    void setDefaultBiddingDeadline() throws NotFoundException, ParseException {
        nl.tudelft.sem.v20232024.team08b.dtos.users.Track trackDTO =
                new nl.tudelft.sem.v20232024.team08b.dtos.users.Track();

        // Set the submission deadline of the track
        Date submissionDeadline = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
                .parse("1970-01-01 22:01:23");
        Long submissionDeadlineLong = submissionDeadline.toInstant().toEpochMilli();
        trackDTO.setDeadline(Integer.valueOf(submissionDeadlineLong.toString()));
        when(externalRepository.getTrack(conferenceID, trackID)).thenReturn(trackDTO);

        // Make sure that our fake track is returned from DB
        doReturn(track).when(tracksService).getTrackWithInsertionToOurRepo(conferenceID, trackID);

        tracksService.setDefaultBiddingDeadline(conferenceID, trackID);

        // Verify that the track was saved to the repository
        verify(trackRepository).save(track);

        // Expected date is exactly 2 days from the submission deadline
        Date expectedDate =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
                .parse("1970-01-03 22:01:23");

        // Get the date that was saved to the DB
        Date calculatedDate = track.getBiddingDeadline();

        assertThat(calculatedDate).isEqualTo(expectedDate);
    }

    @Test
    void getTrackWithInsertionToOurRepo_NoSuchTrack() throws NotFoundException {
        when(verificationService.verifyTrack(conferenceID, trackID)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                tracksService.getTrackWithInsertionToOurRepo(conferenceID, trackID)
        );
    }

    @Test
    void getTrackWithInsertionToOurRepo_PresentInRepo() throws NotFoundException {
        // Assume such track exists
        when(verificationService.verifyTrack(conferenceID, trackID)).thenReturn(true);

        // Assume it is also present in the DB
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(Optional.of(track));

        // Get the result and check if it is what DB returned
        Track result = tracksService.getTrackWithInsertionToOurRepo(conferenceID, trackID);
        assertThat(result).isEqualTo(track);
    }

    @Test
    void getTrackWithInsertionToOurRepo_NotPresentInRepo() throws NotFoundException {
        // Assume such track exists
        when(verificationService.verifyTrack(conferenceID, trackID)).thenReturn(true);

        // Assume it is not present in the DB at first, and then after insertion, it becomes present
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
                Optional.empty(),  // After the first call
                Optional.of(track)      // After the second call
        );

        // Assume that insertion to our DB works fine
        doNothing().when(tracksService).insertTrackToOurDB(conferenceID, trackID);

        // Assume insertion to our DB works
        // Get the result and check if it is what DB returned
        Track result = tracksService.getTrackWithInsertionToOurRepo(conferenceID, trackID);
        assertThat(result).isEqualTo(track);

        // Verify that it was inserted to our DB
        verify(tracksService).insertTrackToOurDB(conferenceID, trackID);
    }

    @Test
    void getTrackWithInsertionToOurRepo_NotPresentInRepoAndErrorLater() throws NotFoundException {
        // Assume such track exists
        when(verificationService.verifyTrack(conferenceID, trackID)).thenReturn(true);

        // Assume it is not present in the DB at first, and then after insertion, it still isn't present
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
                Optional.empty(),  // After the first call
                Optional.empty()      // After the second call
        );

        // Assume that insertion to our DB works fine
        doNothing().when(tracksService).insertTrackToOurDB(conferenceID, trackID);

        // Assume error is thrown
        assertThrows(RuntimeException.class, () ->
                tracksService.getTrackWithInsertionToOurRepo(conferenceID, trackID)
        );
    }

    @Test
    void getBiddingDeadline_AlreadyPresentInRepo() throws NotFoundException, IllegalAccessException {
        // Assume the user is allowed to access the track
        doNothing().when(tracksService).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // Assume the track is in our local DB
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(Optional.of(track));
        doReturn(track).when(tracksService).getTrackWithInsertionToOurRepo(conferenceID, trackID);

        // Set the deadline for the track
        Date biddingDeadline = Date.from(Instant.ofEpochMilli(123L));
        track.setBiddingDeadline(biddingDeadline);

        // Assert the result
        Date result = tracksService.getBiddingDeadline(requesterID, conferenceID, trackID);
        assertThat(result).isEqualTo(biddingDeadline);

        // Verify user access to track was checked
        verify(tracksService).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);
    }

    @Test
    void getBiddingDeadline_NotPresentInRepo() throws NotFoundException, IllegalAccessException {
        // Assume the user is allowed to access the track
        doNothing().when(tracksService).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // Assume the track is not in our local DB
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(Optional.empty());
        doReturn(track).when(tracksService).getTrackWithInsertionToOurRepo(conferenceID, trackID);

        // Set the deadline for the track
        Date biddingDeadline = Date.from(Instant.ofEpochMilli(123L));
        track.setBiddingDeadline(biddingDeadline);

        // Simulate the setting of default deadline
        doNothing().when(tracksService).setDefaultBiddingDeadline(conferenceID, trackID);

        // Assert the result
        Date result = tracksService.getBiddingDeadline(requesterID, conferenceID, trackID);
        assertThat(result).isEqualTo(biddingDeadline);

        // Verify that the track was added to the DB and the the default bidding deadline was set
        verify(tracksService, times(2)).getTrackWithInsertionToOurRepo(conferenceID, trackID);
        verify(tracksService).setDefaultBiddingDeadline(conferenceID, trackID);
    }

    @Test
    void setBiddingDeadline_NotChair() throws NotFoundException, IllegalAccessException {
        Date date = java.sql.Date.valueOf(LocalDate.of(2012, 10, 2));
        // Assume the user is allowed to access the track
        doNothing().when(tracksService).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // Assume the user is not a chair
        when(verificationService.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR))
                .thenReturn(false);
        assertThrows(IllegalAccessException.class, () ->
                tracksService.setBiddingDeadline(requesterID, conferenceID, trackID, date)
        );
    }

    @Test
    void setBiddingDeadline_AllFine() throws NotFoundException, IllegalAccessException {

        List<TrackPhase> goodPhases = List.of(TrackPhase.BIDDING, TrackPhase.SUBMITTING);

        // Assume the user is allowed to access the track
        doNothing().when(tracksService).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // Assume the user is a chair
        when(verificationService.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR))
                .thenReturn(true);

        // Assume phase is right
        doNothing().when(verificationService).verifyTrackPhase(conferenceID, trackID, goodPhases);

        // Assume the repository returns our fake track
        doReturn(track).when(tracksService).getTrackWithInsertionToOurRepo(conferenceID, trackID);
        doReturn(track).when(trackRepository).save(track);

        // Create fake date
        Date date = java.sql.Date.valueOf(LocalDate.of(2012, 10, 2));

        // Call the method
        tracksService.setBiddingDeadline(requesterID, conferenceID, trackID, date);

        // Make sure user access was verified
        verify(tracksService).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // Make sure phase was checked
        verify(verificationService).verifyTrackPhase(conferenceID, trackID, goodPhases);

        // Make sure our track was saved
        verify(trackRepository).save(track);

        // Make sure track date is set
        assertThat(track.getBiddingDeadline()).isEqualTo(date);
    }

}
