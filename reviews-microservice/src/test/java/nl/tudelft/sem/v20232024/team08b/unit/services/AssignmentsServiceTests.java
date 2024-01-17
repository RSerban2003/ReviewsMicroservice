package nl.tudelft.sem.v20232024.team08b.unit.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.AssignmentsService;
import nl.tudelft.sem.v20232024.team08b.application.TracksService;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.application.strategies.AssignmentWithThreeSmallest;
import nl.tudelft.sem.v20232024.team08b.application.verification.PapersVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.TracksVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.communicators.SubmissionsMicroserviceCommunicator;
import nl.tudelft.sem.v20232024.team08b.communicators.UsersMicroserviceCommunicator;
import nl.tudelft.sem.v20232024.team08b.domain.Bid;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperStatus;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummaryWithID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictException;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictOfInterestException;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class AssignmentsServiceTests {
    private final ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
    private final BidRepository bidRepository = Mockito.mock(BidRepository.class);
    private final PapersVerification papersVerification = Mockito.mock(PapersVerification.class);
    private final TracksVerification tracksVerification = Mockito.mock(TracksVerification.class);
    private final UsersVerification usersVerification = Mockito.mock(UsersVerification.class);
    private final SubmissionsMicroserviceCommunicator submissionsCommunicator =
        Mockito.mock(SubmissionsMicroserviceCommunicator.class);
    private final UsersMicroserviceCommunicator usersCommunicator = Mockito.mock(UsersMicroserviceCommunicator.class);
    private final TrackPhaseCalculator trackPhaseCalculator = Mockito.mock(TrackPhaseCalculator.class);
    private final TrackRepository trackRepository = Mockito.mock(TrackRepository.class);
    private final TracksService tracksService = Mockito.mock(TracksService.class);

    private AssignmentsService assignmentsService;

    private final Long reviewerID = 1L;
    private final Long paperID = 2L;
    private final Long requesterID = 3L;
    private final Long trackID = 4L;
    private final Long conferenceID = 5L;
    private Submission fakeSubmission;

    @BeforeEach
    void setUp() {
        assignmentsService = Mockito.spy(
            new AssignmentsService(
                reviewRepository,
                papersVerification,
                tracksVerification,
                    usersVerification,
                    submissionsCommunicator,
                    usersCommunicator,
                    trackPhaseCalculator,
                    trackRepository,
                    tracksService
            )
        );
        assignmentsService.setAutomaticAssignmentStrategy(new AssignmentWithThreeSmallest(
                                                            bidRepository, reviewRepository, submissionsCommunicator));
        fakeSubmission = new Submission();
        fakeSubmission.setEventId(4L);
        fakeSubmission.setTrackId(5L);
    }

    @Test
    void verifyIfRequesterCanAssignRequesterIsChair()
        throws IllegalAccessException, NotFoundException, ConflictOfInterestException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
            .thenReturn(true);

        assertTrue(assignmentsService.verifyIfUserCanAssign(requesterID, paperID, UserRole.CHAIR));

        verify(usersVerification).verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
    }

    @Test
    void verifyIfRequesterCanAssignRequesterIsNotChair() {

        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
            .thenReturn(false);
        assertThrows(IllegalAccessException.class, () ->
            assignmentsService.verifyIfUserCanAssign(requesterID, paperID, UserRole.CHAIR)
        );

        verify(usersVerification).verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
    }

    @Test
    void verifyIfReviewerCanBeAssignedUserNotInTrack() throws NotFoundException, ConflictOfInterestException {

        when(usersVerification.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER))
            .thenReturn(false);
        assertThrows(NotFoundException.class, () ->
            assignmentsService.verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER)
        );

        verify(papersVerification, never()).verifyCOI(anyLong(), anyLong());
    }

    @Test
    void verifyIfReviewerCanBeAssignedUserInTrackNoConflictOfInterest()
        throws NotFoundException, ConflictOfInterestException, IllegalAccessException {

        when(usersVerification.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER))
            .thenReturn(true);
        doNothing().when(papersVerification).verifyCOI(anyLong(), anyLong());
        assertTrue(assignmentsService.verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER));

        verify(papersVerification).verifyCOI(paperID, reviewerID);
    }

    @Test
    void verifyIfReviewerCanBeAssignedUserInTrackConflictOfInterest()
        throws NotFoundException, ConflictOfInterestException {

        when(usersVerification.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER))
            .thenReturn(true);
        doThrow(new ConflictOfInterestException("there is coi")).when(papersVerification).verifyCOI(anyLong(), anyLong());
        assertThrows(ConflictOfInterestException.class, () -> {
            assignmentsService.verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER);
        });
    }

    @Test
    void verifyIfUserCanAssignUndefined() {
        assertThrows(IllegalAccessException.class, () -> {
            assignmentsService.verifyIfUserCanAssign(requesterID, paperID, UserRole.AUTHOR);
        });
    }


    @Test
    void assignManuallySuccessfullyAssigned() throws IllegalAccessException, NotFoundException, ConflictOfInterestException {

        List<TrackPhase> phases = new ArrayList<>();
        TrackPhase phase = TrackPhase.ASSIGNING;
        phases.add(phase);
        doNothing().when(tracksVerification).verifyTrackPhaseThePaperIsIn(paperID, phases);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(usersVerification.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER)).thenReturn(true);
        doNothing().when(tracksVerification).verifyIfTrackExists(paperID);

        // Execute the method
        assertDoesNotThrow(() -> assignmentsService.assignManually(requesterID, reviewerID, paperID));

        // Verify statements
        verify(tracksVerification).verifyTrackPhaseThePaperIsIn(paperID, phases);
        verify(assignmentsService).verifyIfUserCanAssign(requesterID, paperID, UserRole.CHAIR);
        verify(assignmentsService).verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER);
        verify(tracksVerification).verifyIfTrackExists(paperID);

        // Verify that reviewRepository.save is called with the correct argument
        verify(reviewRepository).save(argThat(review -> review.getReviewID().getPaperID().equals(paperID)
            && review.getReviewID().getReviewerID().equals(reviewerID)));
    }

    @Test
    void assignmentsWrongPhase() throws NotFoundException, IllegalAccessException {
        List phases = List.of(TrackPhase.ASSIGNING, TrackPhase.FINAL, TrackPhase.REVIEWING);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        doThrow(
                new IllegalAccessException("Wrong phase")
        ).when(tracksVerification).verifyTrackPhaseThePaperIsIn(paperID, phases);

        assertThrows(IllegalAccessException.class, () -> {
            assignmentsService.assignments(requesterID, paperID);
        });
    }

    @Test
    void assignmentsWrongRole() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);
        assertThrows(IllegalAccessException.class, () -> {
            assignmentsService.assignments(requesterID, paperID);
        });
    }

    @Test
    void assignmentsPaperNotFound() {
        when(papersVerification.verifyPaper(paperID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            assignmentsService.assignments(requesterID, paperID);
        });
    }

    @Test
    void assignmentsSuccessful() throws IllegalAccessException, NotFoundException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);
        List<Review> reviews = new ArrayList<>();
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(reviews);
        assertThat(assignmentsService.assignments(requesterID, paperID).size()).isEqualTo(0);
        Review r1 = new Review();
        Review r2 = new Review();
        Review r3 = new Review();
        ReviewID riD1 = new ReviewID(paperID, 8L);
        ReviewID riD2 = new ReviewID(paperID, reviewerID);
        ReviewID riD3 = new ReviewID(paperID, reviewerID);
        r1.setReviewID(riD1);
        r2.setReviewID(riD2);
        r3.setReviewID(riD3);
        reviews.add(r1);
        assertThat(assignmentsService.assignments(requesterID, paperID).size()).isEqualTo(1);
        reviews.add(r2);
        assertThat(assignmentsService.assignments(requesterID, paperID).size()).isEqualTo(2);
        reviews.add(r3);
        assertThat(assignmentsService.assignments(requesterID, paperID).size()).isEqualTo(3);
    }

    @Test
    void trackNotExist() throws NotFoundException, IllegalAccessException {
        when(tracksVerification.verifyTrack(123L, 123L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            assignmentsService.assignAuto(123L, 123L, 123L);
        });
    }

    @Test
    void userNotExist() throws NotFoundException, IllegalAccessException {
        when(tracksVerification.verifyTrack(123L, 123L)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(123L, 123L, 123L, UserRole.REVIEWER)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            assignmentsService.assignAuto(123L, 123L, 123L);
        });
    }

    @Test
    void notPcChair() throws NotFoundException, IllegalAccessException {
        when(tracksVerification.verifyTrack(123L, 123L)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(123L, 123L, 123L, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(123L, 123L, 123L, UserRole.CHAIR)).thenReturn(false);
        assertThrows(IllegalAccessException.class, () -> {
            assignmentsService.assignAuto(123L, 123L, 123L);
        });
    }

    @Test
    void zeroUser() throws NotFoundException, IllegalAccessException {
        List<Paper> papers = new ArrayList<>();
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        Date date = new Date();
        Optional<Track> trackOptional = Optional.of(new Track(trackID1, date, false, papers));
        Track track = trackOptional.get();
        Paper paper = new Paper(123L, track, PaperStatus.ACCEPTED, false);
        papers.add(paper);
        List<Bid> bids = new ArrayList<>();
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR))
            .thenReturn(true);
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(trackOptional);

        when(bidRepository.findByPaperID(paper.getId())).thenReturn(bids);
        assertThrows(IllegalArgumentException.class, () -> {
            assignmentsService.assignAuto(requesterID, conferenceID, trackID);
        });



    }

    @Test
    void oneUser() throws NotFoundException, IllegalAccessException {
        List<Paper> papers = new ArrayList<>();
        List<Bid> bids = new ArrayList<>();
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        Date date = new Date();
        Optional<Track> trackOptional = Optional.of(new Track(trackID1, date, false, papers));
        Track track = trackOptional.get();
        Paper paper = new Paper(123L, track, PaperStatus.ACCEPTED, false);
        Bid bid = new Bid(paper.getId(), 123L,
            nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        papers.add(paper);
        bids.add(bid);
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR)).thenReturn(true);
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
            trackOptional
        );

        when(bidRepository.findByPaperID(paper.getId())).thenReturn(bids);
        assignmentsService.assignAuto(requesterID, conferenceID, trackID);
        verify(reviewRepository).save(argThat(review -> review.getReviewID().getPaperID().equals(123L)
            && review.getReviewID().getReviewerID().equals(123L)));


    }

    @Test
    void oneUserException() throws NotFoundException, IllegalAccessException {
        List<Paper> papers = new ArrayList<>();
        Paper paper1 = new Paper();
        paper1.setId(paperID);
        papers.add(paper1);
        Submission submission = new Submission();
        submission.setTrackId(trackID);
        submission.setEventId(conferenceID);
        Bid bid1 = new Bid(paperID, 1L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid2 = new Bid(paperID, 2L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        List<Bid> bids = new ArrayList<>();
        bids.add(bid1);
        bids.add(bid2);

        Review review1 = new Review();
        review1.setReviewID(new ReviewID(10L, 1L));

        List<Review> reviews1 = new ArrayList<>();
        reviews1.add(review1);

        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR)).thenReturn(true);
        when(reviewRepository.findByReviewIDReviewerID(1L)).thenReturn(reviews1);

        when(bidRepository.findByPaperID(paperID)).thenReturn(bids);
        when(submissionsCommunicator.getSubmission(10L)).thenThrow((new NotFoundException("Track not found")));
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        Optional<Track> trackOptional = Optional.of(new Track(trackID1, new Date(), false, papers));
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
            trackOptional
        );

        assertThrows(RuntimeException.class, () -> {
            assignmentsService.assignAuto(requesterID, conferenceID, trackID);
        });


    }

    @Test
    void twoUsersAutomatic() throws NotFoundException, IllegalAccessException {
        List<Paper> papers = new ArrayList<>();
        Paper paper1 = new Paper();
        paper1.setId(paperID);
        papers.add(paper1);
        Submission submission = new Submission();
        submission.setTrackId(trackID);
        submission.setEventId(conferenceID);
        Bid bid1 = new Bid(paperID, 1L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid2 = new Bid(paperID, 2L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        List<Bid> bids = new ArrayList<>();
        bids.add(bid1);
        bids.add(bid2);

        Review review1 = new Review();
        review1.setReviewID(new ReviewID(10L, 1L));
        Review review2 = new Review();
        review2.setReviewID(new ReviewID(10L, 2L));
        Review review3 = new Review();
        review3.setReviewID(new ReviewID(20L, 2L));
        List<Review> reviews1 = new ArrayList<>();
        reviews1.add(review1);
        List<Review> reviews2 = new ArrayList<>();
        reviews2.add(review2);
        reviews2.add(review3);
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR)).thenReturn(true);
        when(reviewRepository.findByReviewIDReviewerID(1L)).thenReturn(reviews1);
        when(reviewRepository.findByReviewIDReviewerID(2L)).thenReturn(reviews2);
        when(bidRepository.findByPaperID(paperID)).thenReturn(bids);
        when(submissionsCommunicator.getSubmission(10L)).thenReturn(submission);
        when(submissionsCommunicator.getSubmission(20L)).thenReturn(submission);
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        Optional<Track> trackOptional = Optional.of(new Track(trackID1, new Date(), false, papers));
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
            trackOptional
        );

        assignmentsService.assignAuto(requesterID, conferenceID, trackID);
        verify(reviewRepository).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(1L)));
        verify(reviewRepository).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(2L)));




    }



    @Test
    void fourUsersAutomatic() throws NotFoundException, IllegalAccessException {
        List<Paper> papers = new ArrayList<>();
        Paper paper1 = new Paper();
        paper1.setId(paperID);
        papers.add(paper1);
        Submission submission = new Submission();
        submission.setTrackId(trackID);
        submission.setEventId(conferenceID);
        Bid bid1 = new Bid(paperID, 1L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid2 = new Bid(paperID, 2L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid3 = new Bid(paperID, 3L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid4 = new Bid(paperID, 4L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        List<Bid> bids = new ArrayList<>();
        bids.add(bid1);
        bids.add(bid2);
        bids.add(bid3);
        bids.add(bid4);

        Review review1 = new Review();
        review1.setReviewID(new ReviewID(10L, 1L));
        Review review2 = new Review();
        review2.setReviewID(new ReviewID(10L, 2L));
        Review review3 = new Review();
        review3.setReviewID(new ReviewID(20L, 2L));
        Review review4 = new Review();
        review4.setReviewID(new ReviewID(10L, 3L));
        Review review5 = new Review();
        review5.setReviewID(new ReviewID(20L, 4L));
        List<Review> reviews1 = new ArrayList<>();
        reviews1.add(review1);
        List<Review> reviews2 = new ArrayList<>();
        reviews2.add(review2);
        reviews2.add(review3);
        List<Review> reviews3 = new ArrayList<>();
        reviews3.add(review4);
        List<Review> reviews4 = new ArrayList<>();
        reviews4.add(review5);
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR)).thenReturn(true);
        when(reviewRepository.findByReviewIDReviewerID(1L)).thenReturn(reviews1);
        when(reviewRepository.findByReviewIDReviewerID(2L)).thenReturn(reviews2);
        when(reviewRepository.findByReviewIDReviewerID(3L)).thenReturn(reviews3);
        when(reviewRepository.findByReviewIDReviewerID(4L)).thenReturn(reviews4);
        when(bidRepository.findByPaperID(paperID)).thenReturn(bids);
        when(submissionsCommunicator.getSubmission(10L)).thenReturn(submission);
        when(submissionsCommunicator.getSubmission(20L)).thenReturn(submission);
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        Optional<Track> trackOptional = Optional.of(new Track(trackID1, new Date(), false, papers));
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
            trackOptional
        );

        assignmentsService.assignAuto(requesterID, conferenceID, trackID);
        verify(reviewRepository).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(1L)));
        verify(reviewRepository).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(3L)));
        verify(reviewRepository).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(4L)));
        verify(reviewRepository, never()).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(2L)));




    }

    @Test
    void fourUsersNotInTrack() throws NotFoundException, IllegalAccessException {
        List<Paper> papers = new ArrayList<>();
        Paper paper1 = new Paper();
        paper1.setId(paperID);
        papers.add(paper1);
        Submission submission = new Submission();
        submission.setTrackId(trackID);
        submission.setEventId(conferenceID);
        Submission submission2 = new Submission();
        submission2.setTrackId(trackID + 1);
        submission2.setEventId(conferenceID);
        Bid bid1 = new Bid(paperID, 1L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid2 = new Bid(paperID, 2L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid3 = new Bid(paperID, 3L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid4 = new Bid(paperID, 4L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        List<Bid> bids = new ArrayList<>();
        bids.add(bid1);
        bids.add(bid2);
        bids.add(bid3);
        bids.add(bid4);

        Review review1 = new Review();
        review1.setReviewID(new ReviewID(10L, 1L));
        Review review2 = new Review();
        review2.setReviewID(new ReviewID(40L, 2L));
        Review review3 = new Review();
        review3.setReviewID(new ReviewID(50L, 2L));
        Review review4 = new Review();
        review4.setReviewID(new ReviewID(10L, 3L));
        Review review5 = new Review();
        review5.setReviewID(new ReviewID(20L, 4L));
        List<Review> reviews1 = new ArrayList<>();
        reviews1.add(review1);
        List<Review> reviews2 = new ArrayList<>();
        reviews2.add(review2);
        reviews2.add(review3);
        List<Review> reviews3 = new ArrayList<>();
        reviews3.add(review4);
        List<Review> reviews4 = new ArrayList<>();
        reviews4.add(review5);
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR)).thenReturn(true);
        when(reviewRepository.findByReviewIDReviewerID(1L)).thenReturn(reviews1);
        when(reviewRepository.findByReviewIDReviewerID(2L)).thenReturn(reviews2);
        when(reviewRepository.findByReviewIDReviewerID(3L)).thenReturn(reviews3);
        when(reviewRepository.findByReviewIDReviewerID(4L)).thenReturn(reviews4);
        when(bidRepository.findByPaperID(paperID)).thenReturn(bids);
        when(submissionsCommunicator.getSubmission(10L)).thenReturn(submission);
        when(submissionsCommunicator.getSubmission(20L)).thenReturn(submission);
        when(submissionsCommunicator.getSubmission(20L)).thenReturn(submission);
        when(submissionsCommunicator.getSubmission(40L)).thenReturn(submission2);
        when(submissionsCommunicator.getSubmission(50L)).thenReturn(submission2);
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        Optional<Track> trackOptional = Optional.of(new Track(trackID1, new Date(), false, papers));
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
            trackOptional
        );

        assignmentsService.assignAuto(requesterID, conferenceID, trackID);
        verify(reviewRepository).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(1L)));
        verify(reviewRepository).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(3L)));
        verify(reviewRepository).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(2L)));
        verify(reviewRepository, never()).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(4L)));




    }

    @Test
    void fourUsersNotInConference() throws NotFoundException, IllegalAccessException {
        List<Paper> papers = new ArrayList<>();
        Paper paper1 = new Paper();
        paper1.setId(paperID);
        papers.add(paper1);
        Submission submission = new Submission();
        submission.setTrackId(trackID);
        submission.setEventId(conferenceID);
        Submission submission2 = new Submission();
        submission2.setTrackId(trackID);
        submission2.setEventId(conferenceID + 1);
        Bid bid1 = new Bid(paperID, 1L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid2 = new Bid(paperID, 2L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid3 = new Bid(paperID, 3L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid4 = new Bid(paperID, 4L, nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        List<Bid> bids = new ArrayList<>();
        bids.add(bid1);
        bids.add(bid2);
        bids.add(bid3);
        bids.add(bid4);

        Review review1 = new Review();
        review1.setReviewID(new ReviewID(10L, 1L));
        Review review2 = new Review();
        review2.setReviewID(new ReviewID(40L, 2L));
        Review review3 = new Review();
        review3.setReviewID(new ReviewID(50L, 2L));
        Review review4 = new Review();
        review4.setReviewID(new ReviewID(10L, 3L));
        Review review5 = new Review();
        review5.setReviewID(new ReviewID(20L, 4L));
        List<Review> reviews1 = new ArrayList<>();
        reviews1.add(review1);
        List<Review> reviews2 = new ArrayList<>();
        reviews2.add(review2);
        reviews2.add(review3);
        List<Review> reviews3 = new ArrayList<>();
        reviews3.add(review4);
        List<Review> reviews4 = new ArrayList<>();
        reviews4.add(review5);
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR)).thenReturn(true);
        when(reviewRepository.findByReviewIDReviewerID(1L)).thenReturn(reviews1);
        when(reviewRepository.findByReviewIDReviewerID(2L)).thenReturn(reviews2);
        when(reviewRepository.findByReviewIDReviewerID(3L)).thenReturn(reviews3);
        when(reviewRepository.findByReviewIDReviewerID(4L)).thenReturn(reviews4);
        when(bidRepository.findByPaperID(paperID)).thenReturn(bids);
        when(submissionsCommunicator.getSubmission(10L)).thenReturn(submission);
        when(submissionsCommunicator.getSubmission(20L)).thenReturn(submission);
        when(submissionsCommunicator.getSubmission(20L)).thenReturn(submission);
        when(submissionsCommunicator.getSubmission(40L)).thenReturn(submission2);
        when(submissionsCommunicator.getSubmission(50L)).thenReturn(submission2);
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        Optional<Track> trackOptional = Optional.of(new Track(trackID1, new Date(), false, papers));
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
            trackOptional
        );

        assignmentsService.assignAuto(requesterID, conferenceID, trackID);
        verify(reviewRepository).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(1L)));
        verify(reviewRepository).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(3L)));
        verify(reviewRepository).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(2L)));
        verify(reviewRepository, never()).save(argThat(reviewCheck -> reviewCheck.getReviewID().getPaperID().equals(paperID)
            && reviewCheck.getReviewID().getReviewerID().equals(4L)));




    }



    @Test
    void testGetAssignedPaperUserDoesNotExist() {
        Long requesterID = 1L;
        when(usersVerification.verifyIfUserExists(requesterID)).thenReturn(false);

        Exception e = assertThrows(NotFoundException.class, () -> {
            assignmentsService.getAssignedPapers(requesterID);
        });
        assertEquals("User does not exist!", e.getMessage());
    }

    @Test
    void testGetAssignedPaperNoAssignedPapers() throws NotFoundException {
        when(usersVerification.verifyIfUserExists(requesterID)).thenReturn(true);
        when(reviewRepository.findByReviewIDReviewerID(requesterID)).thenReturn(Collections.emptyList());

        List<PaperSummaryWithID> result = assignmentsService.getAssignedPapers(requesterID);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testGetAssignedPaperWithAssignedPapers() throws NotFoundException {
        ReviewID reviewID = new ReviewID();
        reviewID.setPaperID(paperID);
        nl.tudelft.sem.v20232024.team08b.dtos.review.Paper paper = new nl.tudelft.sem.v20232024.team08b.dtos.review.Paper();
        paper.setTitle("Sample Title");
        paper.setAbstractSection("Sample Abstract");

        Submission submission = new Submission();
        submission.setTitle("Sample Title");
        submission.setAbstract("Sample Abstract");
        submission.setPaper(new byte[0]);
        submission.setKeywords(new ArrayList<>());

        when(usersVerification.verifyIfUserExists(requesterID)).thenReturn(true);
        List<ReviewID> reviewIDs = Collections.singletonList(reviewID);
        List<Review> reviews = List.of(new Review(reviewID, null, null, null, null, null));
        when(reviewRepository.findByReviewIDReviewerID(requesterID)).thenReturn(reviews);
        when(submissionsCommunicator.getSubmission(paperID)).thenReturn(submission);

        List<PaperSummaryWithID> result = assignmentsService.getAssignedPapers(requesterID);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(paperID, result.get(0).getPaperID());
        assertEquals(paper.getTitle(), result.get(0).getTitle());
        assertEquals(paper.getAbstractSection(), result.get(0).getAbstractSection());
    }

    @Test
    void finalizationSuccess() throws NotFoundException {
        final Long requesterID = 1L;
        final TrackID trackID = new TrackID(2L, 3L);
        List<Submission> submissions = new ArrayList<>();
        var s = new Submission();
        s.setSubmissionId(5L);
        submissions.add(s);
        s = new Submission();
        s.setSubmissionId(6L);
        submissions.add(s);

        when(usersCommunicator.getTrack(trackID.getConferenceID(), trackID.getTrackID())).thenReturn(
            new  nl.tudelft.sem.v20232024.team08b.dtos.users.Track());
        when(usersVerification
                .verifyRoleFromTrack(requesterID, trackID.getConferenceID(), trackID.getTrackID(), UserRole.CHAIR))
                .thenReturn(true);
        when(trackPhaseCalculator.getTrackPhase(trackID.getConferenceID(), trackID.getTrackID()))
                .thenReturn(TrackPhase.ASSIGNING);
        when(submissionsCommunicator.getSubmissionsInTrack(trackID, requesterID)).thenReturn(submissions);
        when(reviewRepository.findByReviewIDPaperID(5L)).thenReturn(List.of(new Review(), new Review(), new Review()));
        when(reviewRepository.findByReviewIDPaperID(6L)).thenReturn(List.of(new Review(),
                new Review(), new Review(), new Review()));
        var t = new nl.tudelft.sem.v20232024.team08b.domain.Track();
        when(tracksService.getTrackWithInsertionToOurRepo(trackID.getConferenceID(),
                trackID.getTrackID())).thenReturn(t);

        Assertions.assertDoesNotThrow(() -> assignmentsService.finalization(requesterID, trackID));
        Assertions.assertTrue(t.getReviewersHaveBeenFinalized());
    }

    @Test
    void finalizationWhenTrackDoesNotExistThrowsNotFoundException() throws NotFoundException {
        Long requesterID = 1L;
        TrackID trackID = new TrackID(2L, 3L);

        when(usersCommunicator.getTrack(trackID.getConferenceID(), trackID.getTrackID()))
                .thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class, () -> assignmentsService.finalization(requesterID, trackID));
    }

    @Test
    void finalizationWhenRequesterIsNotPCChairThrowsForbiddenAccessException() throws NotFoundException {
        Long requesterID = 1L;
        TrackID trackID = new TrackID(2L, 3L);

        when(usersCommunicator.getTrack(trackID.getConferenceID(), trackID.getTrackID())).thenReturn(
            new nl.tudelft.sem.v20232024.team08b.dtos.users.Track());
        when(usersVerification
                .verifyRoleFromTrack(requesterID, trackID.getConferenceID(), trackID.getTrackID(), UserRole.CHAIR))
                .thenReturn(false);

        Assertions.assertThrows(ForbiddenAccessException.class,
                () -> assignmentsService.finalization(requesterID, trackID));
    }

    @Test
    void finalizationWhenTrackIsNotInAssigningPhaseThrowsConflictException() throws NotFoundException {
        Long requesterID = 1L;
        TrackID trackID = new TrackID(2L, 3L);

        when(usersCommunicator.getTrack(trackID.getConferenceID(), trackID.getTrackID())).thenReturn(
            new nl.tudelft.sem.v20232024.team08b.dtos.users.Track());
        when(usersVerification.verifyRoleFromTrack(requesterID, trackID.getConferenceID(),
                trackID.getTrackID(), UserRole.CHAIR)).thenReturn(true);
        when(trackPhaseCalculator.getTrackPhase(trackID.getConferenceID(), trackID.getTrackID()))
                .thenReturn(TrackPhase.REVIEWING);

        Assertions.assertThrows(ConflictException.class,
                () -> assignmentsService.finalization(requesterID, trackID));
    }

    @Test
    void finalizationWhenNotEnoughReviewersAssignedToEachPaperThrowsConflictException()
            throws NotFoundException {
        final Long requesterID = 1L;
        final TrackID trackID = new TrackID(1L, 1L);
        List<Submission> submissions = new ArrayList<>();
        var s = new Submission();
        s.setSubmissionId(5L);
        submissions.add(s);
        s = new Submission();
        s.setSubmissionId(6L);
        submissions.add(s);

        when(usersCommunicator.getTrack(trackID.getConferenceID(), trackID.getTrackID()))
                .thenReturn(new  nl.tudelft.sem.v20232024.team08b.dtos.users.Track());
        when(usersVerification.verifyRoleFromTrack(requesterID, trackID.getConferenceID(),
                trackID.getTrackID(), UserRole.CHAIR)).thenReturn(true);
        when(trackPhaseCalculator.getTrackPhase(trackID.getConferenceID(), trackID.getTrackID()))
                .thenReturn(TrackPhase.ASSIGNING);
        when(submissionsCommunicator.getSubmissionsInTrack(trackID, requesterID)).thenReturn(submissions);
        when(reviewRepository.findByReviewIDPaperID(5L))
                .thenReturn(List.of(new Review(), new Review(), new Review()));
        when(reviewRepository.findByReviewIDPaperID(6L)).thenReturn(List.of(new Review(),
                new Review()));

        Assertions.assertThrows(ConflictException.class, () -> assignmentsService.finalization(requesterID, trackID));
    }
}
