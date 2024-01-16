package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.PapersService;
import nl.tudelft.sem.v20232024.team08b.application.TracksService;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.application.verification.TracksVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.communicators.SubmissionsMicroserviceCommunicator;
import nl.tudelft.sem.v20232024.team08b.communicators.UsersMicroserviceCommunicator;
import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperStatus;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummaryWithID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackAnalytics;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TracksServicesTests {
    private final TracksVerification tracksVerification = Mockito.mock(TracksVerification.class);
    private final UsersVerification usersVerification = Mockito.mock(UsersVerification.class);
    private final TrackPhaseCalculator trackPhaseCalculator = Mockito.mock(TrackPhaseCalculator.class);
    private final TrackRepository trackRepository = Mockito.mock(TrackRepository.class);
    private final SubmissionsMicroserviceCommunicator submissionsCommunicator =
        Mockito.mock(SubmissionsMicroserviceCommunicator.class);
    private final PapersService papersService = Mockito.mock(PapersService.class);
    private final UsersMicroserviceCommunicator usersCommunicator = Mockito.mock(UsersMicroserviceCommunicator.class);

    private final TracksService tracksService = Mockito.spy(
            new TracksService(
                    trackPhaseCalculator,
                    trackRepository,
                    submissionsCommunicator,
                    tracksVerification,
                    usersVerification,
                usersCommunicator, papersService
            )
    );

    private Long requesterID = 0L;
    private Long conferenceID = 1L;
    private Long trackID = 2L;
    private Track track;
    @BeforeEach
    void init() {
        track = new Track();
    }

    @Test
    void getTrackPhase() throws NotFoundException, IllegalAccessException {
        // Assume that the current phase is bidding
        when(trackPhaseCalculator.getTrackPhase(conferenceID, trackID)).thenReturn(TrackPhase.BIDDING);

        // Assume that the provided input to function is valid
        doNothing().when(tracksVerification).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // Make sure, that the service returns the same result that the calculator returns
        assertThat(
                tracksService.getTrackPhase(requesterID, conferenceID, trackID)
        ).isEqualTo(TrackPhase.BIDDING);
        verify(tracksVerification).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);
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
        when(usersCommunicator.getTrack(conferenceID, trackID)).thenReturn(trackDTO);

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
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                tracksService.getTrackWithInsertionToOurRepo(conferenceID, trackID)
        );
    }

    @Test
    void getTrackWithInsertionToOurRepo_PresentInRepo() throws NotFoundException {
        // Assume such track exists
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);

        // Assume it is also present in the DB
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(Optional.of(track));

        // Get the result and check if it is what DB returned
        Track result = tracksService.getTrackWithInsertionToOurRepo(conferenceID, trackID);
        assertThat(result).isEqualTo(track);
    }

    @Test
    void getTrackWithInsertionToOurRepo_NotPresentInRepo() throws NotFoundException {
        // Assume such track exists
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);

        // Assume it is not present in the DB at first, and then after insertion, it becomes present
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
                Optional.empty(),  // After the first call
                Optional.of(track)      // After the second call
        );

        // Assume that insertion to our DB works fine
        doNothing().when(tracksVerification).insertTrack(conferenceID, trackID);

        // Assume insertion to our DB works
        // Get the result and check if it is what DB returned
        Track result = tracksService.getTrackWithInsertionToOurRepo(conferenceID, trackID);
        assertThat(result).isEqualTo(track);

        // Verify that it was inserted to our DB
        verify(tracksVerification).insertTrack(conferenceID, trackID);
    }

    @Test
    void getTrackWithInsertionToOurRepo_NotPresentInRepoAndErrorLater() throws NotFoundException {
        // Assume such track exists
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);

        // Assume it is not present in the DB at first, and then after insertion, it still isn't present
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
                Optional.empty(),  // After the first call
                Optional.empty()      // After the second call
        );

        // Assume that insertion to our DB works fine
        doNothing().when(tracksVerification).insertTrack(conferenceID, trackID);

        // Assume error is thrown
        assertThrows(RuntimeException.class, () ->
                tracksService.getTrackWithInsertionToOurRepo(conferenceID, trackID)
        );
    }

    @Test
    void getBiddingDeadline_AlreadyPresentInRepo() throws NotFoundException, IllegalAccessException {
        // Assume the user is allowed to access the track
        doNothing().when(tracksVerification).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

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
        verify(tracksVerification).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);
    }

    @Test
    void getBiddingDeadline_NotPresentInRepo() throws NotFoundException, IllegalAccessException {
        // Assume the user is allowed to access the track
        doNothing().when(tracksVerification).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

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
        doNothing().when(tracksVerification).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // Assume the user is not a chair
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR))
                .thenReturn(false);
        assertThrows(IllegalAccessException.class, () ->
                tracksService.setBiddingDeadline(requesterID, conferenceID, trackID, date)
        );
    }

    @Test
    void setBiddingDeadline_AllFine() throws NotFoundException, IllegalAccessException {

        List<TrackPhase> goodPhases = List.of(TrackPhase.BIDDING, TrackPhase.SUBMITTING);

        // Assume the user is allowed to access the track
        doNothing().when(tracksVerification).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // Assume the user is a chair
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR))
                .thenReturn(true);

        // Assume phase is right
        doNothing().when(tracksVerification).verifyTrackPhase(conferenceID, trackID, goodPhases);

        // Assume the repository returns our fake track
        doReturn(track).when(tracksService).getTrackWithInsertionToOurRepo(conferenceID, trackID);
        doReturn(track).when(trackRepository).save(track);

        // Create fake date
        Date date = java.sql.Date.valueOf(LocalDate.of(2012, 10, 2));

        // Call the method
        tracksService.setBiddingDeadline(requesterID, conferenceID, trackID, date);

        // Make sure user access was verified
        verify(tracksVerification).verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // Make sure phase was checked
        verify(tracksVerification).verifyTrackPhase(conferenceID, trackID, goodPhases);

        // Make sure our track was saved
        verify(trackRepository).save(track);

        // Make sure track date is set
        assertThat(track.getBiddingDeadline()).isEqualTo(date);
    }

    @Test
    void testGetAnalyticsSuccess() throws NotFoundException, ForbiddenAccessException, IllegalAccessException {
        TrackID trackID = new TrackID();
        trackID.setConferenceID(1L);
        trackID.setTrackID(2L);
        final Long requesterID = 3L;

        List<Submission> submissions = new ArrayList<>();
        Submission submission1 = new Submission();
        submission1.setSubmissionId(1L);
        submissions.add(submission1);

        Submission submission2 = new Submission();
        submission2.setSubmissionId(2L);
        submissions.add(submission2);

        Submission submission3 = new Submission();
        submission3.setSubmissionId(3L);
        submissions.add(submission3);

        Submission submission4 = new Submission();
        submission4.setSubmissionId(4L);
        submissions.add(submission4);

        Submission submission5 = new Submission();
        submission5.setSubmissionId(5L);
        submissions.add(submission5);

        Submission submission6 = new Submission();
        submission6.setSubmissionId(6L);
        submissions.add(submission6);

        when(usersVerification.verifyRoleFromTrack(requesterID, trackID.getConferenceID(),
                trackID.getTrackID(), UserRole.CHAIR)).thenReturn(true);

        when(submissionsCommunicator.getSubmissionsInTrack(trackID, requesterID)).thenReturn(submissions);

        when(papersService.getState(requesterID, 1L)).thenReturn(PaperStatus.ACCEPTED);
        when(papersService.getState(requesterID, 2L)).thenReturn(PaperStatus.REJECTED);
        when(papersService.getState(requesterID, 3L)).thenReturn(PaperStatus.NOT_DECIDED);
        when(papersService.getState(requesterID, 4L)).thenReturn(PaperStatus.ACCEPTED);
        when(papersService.getState(requesterID, 5L)).thenReturn(PaperStatus.ACCEPTED);
        when(papersService.getState(requesterID, 6L)).thenReturn(PaperStatus.NOT_DECIDED);

        TrackAnalytics result = tracksService.getAnalytics(trackID, requesterID);

        Assertions.assertEquals(3, result.getAccepted());
        Assertions.assertEquals(1, result.getRejected());
        Assertions.assertEquals(2, result.getUnknown());
    }

    @Test
    void testGetAnalyticsNotFoundException() throws NotFoundException {
        TrackID trackID = new TrackID();
        trackID.setConferenceID(1L);
        trackID.setTrackID(2L);
        Long requesterID = 3L;

        when(usersVerification.verifyRoleFromTrack(requesterID, trackID.getConferenceID(),
                trackID.getTrackID(), UserRole.CHAIR)).thenReturn(true);

        when(submissionsCommunicator.getSubmissionsInTrack(trackID, requesterID)).thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class, () -> {
            tracksService.getAnalytics(trackID, requesterID);
        });
    }

    @Test
    void testGetAnalyticsForbiddenAccessException() {
        TrackID trackID = new TrackID();
        trackID.setConferenceID(1L);
        trackID.setTrackID(2L);
        Long requesterID = 3L;

        when(usersVerification.verifyRoleFromTrack(requesterID, trackID.getConferenceID(),
                trackID.getTrackID(), UserRole.CHAIR)).thenReturn(false);

        Assertions.assertThrows(ForbiddenAccessException.class, () -> {
            tracksService.getAnalytics(trackID, requesterID);
        });
    }

    @Test
    void testGetAnalyticsRuntimeException()
            throws NotFoundException, IllegalAccessException {
        TrackID trackID = new TrackID();
        trackID.setConferenceID(1L);
        trackID.setTrackID(1L);
        Long requesterID = 1L;

        List<Submission> submissions = new ArrayList<>();
        Submission submission1 = new Submission();
        submission1.setSubmissionId(1L);
        submissions.add(submission1);

        when(usersVerification.verifyRoleFromTrack(requesterID, trackID.getConferenceID(),
                trackID.getTrackID(), UserRole.CHAIR)).thenReturn(true);

        when(submissionsCommunicator.getSubmissionsInTrack(trackID, requesterID)).thenReturn(submissions);

        when(papersService.getState(1L, requesterID)).thenThrow(new IllegalAccessException());

        Assertions.assertThrows(RuntimeException.class, () -> {
            tracksService.getAnalytics(trackID, requesterID);
        });
    }

    @Test
    void getPaperNoPermission() {
        Long requesterID = 3L;
        Long conferenceID = 4L;
        Long trackID = 5L;
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID,
            trackID, UserRole.CHAIR)).thenReturn(false);
        assertThrows(ForbiddenAccessException.class, () -> {
            tracksService.getPapers(requesterID, conferenceID, trackID);
        });
    }

    @Test
    void getPaperNoTrack() {
        Long requesterID = 3L;
        Long conferenceID = 4L;
        Long trackID = 5L;
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID,
            trackID, UserRole.CHAIR)).thenReturn(true);
        when(tracksVerification.verifyTrack(conferenceID,
            trackID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            tracksService.getPapers(requesterID, conferenceID, trackID);
        });
    }

    @Test
    void getZeroPaper() throws NotFoundException, ForbiddenAccessException {
        Long requesterID = 3L;
        Long conferenceID = 4L;
        Long trackID = 5L;
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID,
            trackID, UserRole.CHAIR)).thenReturn(true);
        when(tracksVerification.verifyTrack(conferenceID,
            trackID)).thenReturn(true);
        when(externalRepository.getSubmissionsInTrack(trackID1, requesterID)).thenReturn(new ArrayList<>());
        List<PaperSummaryWithID> papers = new ArrayList<>();
        assertThat(tracksService.getPapers(requesterID, conferenceID, trackID)).isEqualTo(papers);
    }

    @Test
    void getOnePaper() throws NotFoundException, ForbiddenAccessException {
        Long requesterID = 3L;
        Long conferenceID = 4L;
        Long trackID = 5L;
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID,
            trackID, UserRole.CHAIR)).thenReturn(true);
        when(tracksVerification.verifyTrack(conferenceID,
            trackID)).thenReturn(true);
        Submission submission1 = new Submission();
        submission1.setTitle("abc");
        submission1.setAbstract("def");
        submission1.setSubmissionId(1L);
        List<Submission> submissions = new ArrayList<>();
        submissions.add(submission1);
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        when(externalRepository.getSubmissionsInTrack(trackID1, requesterID)).thenReturn(submissions);
        var paper1 = new PaperSummaryWithID();
        paper1.setPaperID(1L);
        paper1.setTitle("abc");
        paper1.setAbstractSection("def");
        List<PaperSummaryWithID> papers = new ArrayList<>();
        papers.add(paper1);
        var papersSolution = tracksService.getPapers(requesterID, conferenceID, trackID);
        var idExample = papers.stream().map(PaperSummaryWithID::getPaperID).toArray();
        var idSolution = papersSolution.stream().map(PaperSummaryWithID::getPaperID).toArray();
        assertThat(idSolution).isEqualTo(idExample);
        var titleExample = papers.stream().map(PaperSummaryWithID::getTitle).toArray();
        var titleSolution = papersSolution.stream().map(PaperSummaryWithID::getTitle).toArray();
        assertThat(titleSolution).isEqualTo(titleExample);
        var abstractExample = papers.stream().map(PaperSummaryWithID::getAbstractSection).toArray();
        var abstractSolution = papersSolution.stream().map(PaperSummaryWithID::getAbstractSection).toArray();
        assertThat(abstractSolution).isEqualTo(abstractExample);
    }

    @Test
    void getTwoPapers() throws NotFoundException, ForbiddenAccessException {
        Long requesterID = 3L;
        Long conferenceID = 4L;
        Long trackID = 5L;
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID,
            trackID, UserRole.CHAIR)).thenReturn(true);
        when(tracksVerification.verifyTrack(conferenceID,
            trackID)).thenReturn(true);
        Submission submission1 = new Submission();
        submission1.setTitle("abc");
        submission1.setAbstract("def");
        submission1.setSubmissionId(1L);
        Submission submission2 = new Submission();
        submission2.setTitle("zyx");
        submission2.setAbstract("wvu");
        submission2.setSubmissionId(2L);
        List<Submission> submissions = new ArrayList<>();
        submissions.add(submission1);
        submissions.add(submission2);
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        when(externalRepository.getSubmissionsInTrack(trackID1, requesterID)).thenReturn(submissions);
        var paper1 = new PaperSummaryWithID();
        paper1.setPaperID(1L);
        paper1.setTitle("abc");
        paper1.setAbstractSection("def");
        var paper2 = new PaperSummaryWithID();
        paper2.setPaperID(2L);
        paper2.setTitle("zyx");
        paper2.setAbstractSection("wvu");
        List<PaperSummaryWithID> papers = new ArrayList<>();
        papers.add(paper1);
        papers.add(paper2);
        var papersSolution = tracksService.getPapers(requesterID, conferenceID, trackID);
        var idExample = papers.stream().map(PaperSummaryWithID::getPaperID).toArray();
        var idSolution = papersSolution.stream().map(PaperSummaryWithID::getPaperID).toArray();
        assertThat(idSolution).isEqualTo(idExample);
        var titleExample = papers.stream().map(PaperSummaryWithID::getTitle).toArray();
        var titleSolution = papersSolution.stream().map(PaperSummaryWithID::getTitle).toArray();
        assertThat(titleSolution).isEqualTo(titleExample);
        var abstractExample = papers.stream().map(PaperSummaryWithID::getAbstractSection).toArray();
        var abstractSolution = papersSolution.stream().map(PaperSummaryWithID::getAbstractSection).toArray();
        assertThat(abstractSolution).isEqualTo(abstractExample);
    }
}
